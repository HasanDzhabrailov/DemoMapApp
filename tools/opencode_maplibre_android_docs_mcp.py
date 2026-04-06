#!/usr/bin/env python3

import html
import json
import re
import sys
import urllib.parse
import urllib.request
from dataclasses import dataclass
from html.parser import HTMLParser


USER_AGENT = "DemoMapApp-OpenCode-MCP/1.0"
PROTOCOL_VERSION = "2025-03-26"
REQUEST_TIMEOUT_SECONDS = 30
MAX_RESULTS = 5
MAPLIBRE_ANDROID_API_ROOT = "https://maplibre.org/maplibre-native/android/api/"
MAPLIBRE_ANDROID_API_INDEX = urllib.parse.urljoin(MAPLIBRE_ANDROID_API_ROOT, "index.html")
PRIORITY_PACKAGES = [
    "org.maplibre.android.maps",
    "org.maplibre.android.style.sources",
    "org.maplibre.android.style.layers",
    "org.maplibre.android.style.expressions",
]
SYMBOL_PACKAGE_HINTS = {
    "MapView": ["org.maplibre.android.maps"],
    "Style": ["org.maplibre.android.maps"],
    "GeoJsonSource": ["org.maplibre.android.style.sources"],
    "SymbolLayer": ["org.maplibre.android.style.layers"],
    "LineLayer": ["org.maplibre.android.style.layers"],
    "FillLayer": ["org.maplibre.android.style.layers"],
    "CircleLayer": ["org.maplibre.android.style.layers"],
    "Source": ["org.maplibre.android.style.sources"],
    "Layer": ["org.maplibre.android.style.layers"],
}


@dataclass
class SearchEntry:
    title: str
    url: str
    kind: str
    source: str
    score: int


@dataclass
class ApiEntry:
    symbol: str
    url: str
    kind: str
    package_name: str


_CACHE: dict[str, object] = {}


def fetch_text(url: str) -> str:
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(request, timeout=REQUEST_TIMEOUT_SECONDS) as response:
        charset = response.headers.get_content_charset() or "utf-8"
        return response.read().decode(charset, errors="replace")


def write_message(message: dict) -> None:
    sys.stdout.write(json.dumps(message, ensure_ascii=True) + "\n")
    sys.stdout.flush()


def tool_text(text: str, is_error: bool = False) -> dict:
    return {
        "content": [{"type": "text", "text": text}],
        "isError": is_error,
    }


def normalize(text: str) -> str:
    return re.sub(r"[^a-z0-9]+", " ", text.lower()).strip()


def tokenize(query: str) -> list[str]:
    collapsed = query.strip()
    tokens = re.findall(r"[A-Za-z0-9]+", collapsed.lower())
    if collapsed:
        tokens.append(re.sub(r"\s+", "", collapsed.lower()))
    deduped: list[str] = []
    for token in tokens:
        if len(token) < 2:
            continue
        if token not in deduped:
            deduped.append(token)
    return deduped


def valid_maplibre_url(url: str) -> bool:
    return url.startswith(MAPLIBRE_ANDROID_API_ROOT)


def strip_tags(value: str) -> str:
    text = re.sub(r"<[^>]+>", " ", value)
    text = html.unescape(text)
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def to_absolute_url(base_url: str, relative_url: str) -> str:
    decoded = html.unescape(relative_url).strip()
    encoded = urllib.parse.quote(decoded, safe="/:#?&=%.-_")
    return urllib.parse.urljoin(base_url, encoded)


def extract_api_symbol(query: str) -> str:
    raw_terms = re.findall(r"[A-Za-z0-9_.]+", query)
    for term in reversed(raw_terms):
        if "." in term:
            part = term.split(".")[-1]
            if re.match(r"[A-Z][A-Za-z0-9]+$", part):
                return part
        if re.match(r"[A-Z][A-Za-z0-9]+$", term):
            return term
    return ""


def extract_member_symbol(query: str) -> str:
    dotted_members = re.findall(r"[A-Z][A-Za-z0-9]+\.([a-z][A-Za-z0-9]+)", query)
    if dotted_members:
        return dotted_members[-1]

    raw_terms = re.findall(r"[A-Za-z0-9_.]+", query)
    for term in reversed(raw_terms):
        if re.match(r"[a-z]+[A-Za-z0-9]+$", term):
            return term
    return ""


def normalize_symbol(symbol: str) -> str:
    return normalize(symbol).replace(" ", "")


def contains_member_hint(query: str) -> bool:
    lowered = query.lower()
    return "." in query or "(" in query or any(keyword in lowered for keyword in ["method", "function", "property"])


def exact_symbol_match(expected: str, actual: str) -> bool:
    return bool(expected) and normalize_symbol(expected) == normalize_symbol(actual)


def extract_fetch_member_symbol(section_hint: str, url: str) -> str:
    symbol = extract_member_symbol(section_hint)
    if symbol:
        return symbol
    path = urllib.parse.urlparse(url).path
    tail = path.rstrip("/").split("/")[-1]
    if tail.endswith(".html") and tail != "index.html":
        return tail[:-5]
    return ""


def package_priority_bonus(package_name: str, query: str, symbol: str) -> int:
    bonus = 0
    normalized_query = normalize(query)

    if package_name in PRIORITY_PACKAGES:
        bonus += 10
    if package_name in (SYMBOL_PACKAGE_HINTS.get(symbol) or []):
        bonus += 80
    if "source" in normalized_query and package_name == "org.maplibre.android.style.sources":
        bonus += 35
    if "layer" in normalized_query and package_name == "org.maplibre.android.style.layers":
        bonus += 35
    if "style" in normalized_query and package_name == "org.maplibre.android.maps":
        bonus += 25
    if "mapview" in normalized_query and package_name == "org.maplibre.android.maps":
        bonus += 25
    if "expression" in normalized_query and package_name == "org.maplibre.android.style.expressions":
        bonus += 25

    return bonus


def score_entry(query: str, tokens: list[str], title: str, url: str) -> int:
    haystacks = [normalize(title), normalize(url), normalize(urllib.parse.urlparse(url).path)]
    score = 0
    collapsed_query = normalize(query)

    if collapsed_query and collapsed_query in haystacks[0]:
        score += 30
    if collapsed_query and collapsed_query in haystacks[1]:
        score += 20

    for token in tokens:
        for haystack in haystacks:
            if token == haystack:
                score += 12
            elif token in haystack:
                score += 5

    return score


def format_results(results: list[SearchEntry]) -> str:
    if not results:
        return "No MapLibre Android API matches were found. Refine the query and try again."

    lines = []
    for index, result in enumerate(results, start=1):
        lines.append(
            f"{index}. [{result.kind}] {result.title}\n"
            f"   URL: {result.url}\n"
            f"   Source: {result.source}\n"
            f"   Score: {result.score}"
        )
    return "\n\n".join(lines)


def load_package_catalog() -> list[ApiEntry]:
    if "package_catalog" in _CACHE:
        return _CACHE["package_catalog"]  # type: ignore[return-value]

    raw_html = fetch_text(MAPLIBRE_ANDROID_API_INDEX)
    entries: list[ApiEntry] = []
    seen: set[str] = set()
    for relative_url, package_name in re.findall(r'<a href="([^"]+org\.maplibre\.android[^"]*/index\.html)">([^<]+)</a>', raw_html):
        package_name = package_name.strip()
        absolute_url = to_absolute_url(MAPLIBRE_ANDROID_API_INDEX, relative_url)
        if not package_name.startswith("org.maplibre.android"):
            continue
        if absolute_url in seen:
            continue
        seen.add(absolute_url)
        entries.append(ApiEntry(symbol=package_name, url=absolute_url, kind="package", package_name=package_name))

    _CACHE["package_catalog"] = entries
    return entries


def package_candidates(query: str, symbol: str) -> list[ApiEntry]:
    catalog = load_package_catalog()
    tokens = tokenize(query)
    selected: list[tuple[int, ApiEntry]] = []

    for entry in catalog:
        score = score_entry(query, tokens, entry.symbol, entry.url)
        score += package_priority_bonus(entry.package_name, query, symbol)
        if score <= 0:
            continue
        selected.append((score, entry))

    selected.sort(key=lambda item: (-item[0], item[1].url))

    if selected:
        return [entry for _, entry in selected[:6]]

    fallback = [entry for entry in catalog if entry.package_name in PRIORITY_PACKAGES]
    return fallback[:4]


def load_package_types(package_entry: ApiEntry) -> list[ApiEntry]:
    cache_key = f"package_types:{package_entry.url}"
    if cache_key in _CACHE:
        return _CACHE[cache_key]  # type: ignore[return-value]

    raw_html = fetch_text(package_entry.url)
    entries: list[ApiEntry] = []
    seen: set[str] = set()
    pattern = re.compile(
        r'anchor-label="([^"]+)"[^>]*>\s*</a>\s*<div class="table-row".*?<div><a href="([^"]+)">',
        re.DOTALL,
    )
    for symbol, relative_url in pattern.findall(raw_html):
        absolute_url = to_absolute_url(package_entry.url, relative_url)
        if absolute_url in seen:
            continue
        seen.add(absolute_url)
        entries.append(ApiEntry(symbol=symbol.strip(), url=absolute_url, kind="class", package_name=package_entry.package_name))

    _CACHE[cache_key] = entries
    return entries


def load_class_members(class_entry: ApiEntry) -> list[ApiEntry]:
    cache_key = f"class_members:{class_entry.url}"
    if cache_key in _CACHE:
        return _CACHE[cache_key]  # type: ignore[return-value]

    raw_html = fetch_text(class_entry.url)
    entries: list[ApiEntry] = []
    seen: set[str] = set()
    pattern = re.compile(
        r'anchor-label="([^"]+)"[^>]*>\s*</a>\s*<div class="table-row".*?<div><a href="([^"]+\.(?:html))">',
        re.DOTALL,
    )
    for symbol, relative_url in pattern.findall(raw_html):
        absolute_url = to_absolute_url(class_entry.url, relative_url)
        if absolute_url == class_entry.url or absolute_url in seen:
            continue
        seen.add(absolute_url)
        entries.append(ApiEntry(symbol=symbol.strip(), url=absolute_url, kind="member", package_name=class_entry.package_name))

    _CACHE[cache_key] = entries
    return entries


def maybe_resolve_member_fetch_url(url: str, section_hint: str, raw_html: str) -> str:
    member_symbol = extract_fetch_member_symbol(section_hint, url)
    if not member_symbol:
        return url

    page_type_match = re.search(r'data-page-type="([^"]+)"', raw_html, re.IGNORECASE)
    page_type = page_type_match.group(1).strip().lower() if page_type_match else ""
    if page_type != "classlike":
        return url

    class_name_match = re.search(r"<title>(.*?)</title>", raw_html, re.IGNORECASE | re.DOTALL)
    class_symbol = html.unescape(class_name_match.group(1).strip()) if class_name_match else ""
    package_match = re.search(r'<div class="breadcrumbs">.*?<a href="\.\./index.html">([^<]+)</a>', raw_html, re.IGNORECASE | re.DOTALL)
    package_name = package_match.group(1).strip() if package_match else ""
    if not class_symbol or not package_name:
        return url

    class_entry = ApiEntry(symbol=class_symbol, url=url, kind="class", package_name=package_name)
    for member_entry in load_class_members(class_entry):
        if exact_symbol_match(member_symbol, member_entry.symbol) or normalize_symbol(member_symbol) in normalize_symbol(member_entry.symbol):
            return member_entry.url
    return url


def symbol_search(query: str, limit: int) -> list[SearchEntry]:
    tokens = tokenize(query)
    class_symbol = extract_api_symbol(query)
    member_symbol = extract_member_symbol(query) if contains_member_hint(query) else ""
    package_pool = package_candidates(query, class_symbol)
    results: list[SearchEntry] = []

    for package_entry in package_pool:
        package_score = score_entry(query, tokens, package_entry.symbol, package_entry.url)
        package_score += package_priority_bonus(package_entry.package_name, query, class_symbol)
        if member_symbol:
            package_score -= 25
        if package_score > 0:
            results.append(
                SearchEntry(
                    title=package_entry.symbol,
                    url=package_entry.url,
                    kind="package",
                    source="maplibre-package-catalog",
                    score=package_score,
                )
            )

        for class_entry in load_package_types(package_entry):
            class_score = score_entry(query, tokens, class_entry.symbol, class_entry.url)
            class_score += package_priority_bonus(class_entry.package_name, query, class_entry.symbol)

            if class_symbol:
                if exact_symbol_match(class_symbol, class_entry.symbol):
                    class_score += 120
                elif normalize(class_symbol) in normalize(class_entry.symbol):
                    class_score += 30
            if member_symbol and exact_symbol_match(class_symbol, class_entry.symbol):
                class_score -= 45
            if class_entry.symbol in {"MapView", "Style", "GeoJsonSource", "SymbolLayer", "LineLayer", "FillLayer", "CircleLayer"}:
                class_score += 10

            if class_score <= 0:
                continue

            results.append(
                SearchEntry(
                    title=f"{class_entry.symbol} ({class_entry.package_name})",
                    url=class_entry.url,
                    kind="class",
                    source="maplibre-package-page",
                    score=class_score,
                )
            )

            if not member_symbol:
                continue
            if class_symbol and class_entry.symbol != class_symbol:
                continue

            for member_entry in load_class_members(class_entry):
                member_score = score_entry(query, tokens, member_entry.symbol, member_entry.url)
                if exact_symbol_match(member_symbol, member_entry.symbol):
                    member_score += 220
                elif normalize(member_symbol) in normalize(member_entry.symbol):
                    member_score += 40
                if exact_symbol_match(class_symbol, class_entry.symbol):
                    member_score += 80
                elif class_entry.symbol == class_symbol:
                    member_score += 40
                if member_score <= 0:
                    continue
                results.append(
                    SearchEntry(
                        title=f"{class_entry.symbol}.{member_entry.symbol} ({class_entry.package_name})",
                        url=member_entry.url,
                        kind="member",
                        source="maplibre-class-page",
                        score=member_score,
                    )
                )

    deduped: dict[str, SearchEntry] = {}
    for entry in sorted(results, key=lambda item: (-item.score, item.url)):
        deduped.setdefault(entry.url, entry)
    return list(deduped.values())[:limit]


def extract_heading_sections_from_text(text: str) -> list[tuple[str, str]]:
    sections: list[tuple[str, str]] = []
    current_heading = "Introduction"
    current_lines: list[str] = []

    for raw_line in text.splitlines():
        line = raw_line.rstrip()
        heading_match = re.match(r"^(#{1,6})\s+(.*\S)\s*$", line)
        if heading_match:
            content = "\n".join(current_lines).strip()
            if content:
                sections.append((current_heading, content))
            current_heading = heading_match.group(2)
            current_lines = []
            continue
        current_lines.append(line)

    content = "\n".join(current_lines).strip()
    if content:
        sections.append((current_heading, content))
    return sections


class SectionHTMLParser(HTMLParser):
    BLOCK_TAGS = {
        "p",
        "div",
        "section",
        "article",
        "ul",
        "ol",
        "li",
        "pre",
        "code",
        "blockquote",
        "table",
        "tr",
        "td",
        "th",
        "br",
    }
    HEADING_TAGS = {"h1", "h2", "h3", "h4"}
    SKIP_TAGS = {"script", "style", "noscript", "svg"}

    def __init__(self) -> None:
        super().__init__()
        self.in_skip = 0
        self.in_main = 0
        self.current_heading = "Introduction"
        self.current_lines: list[str] = []
        self.sections: list[tuple[str, str]] = []
        self.heading_buffer: list[str] = []

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        if tag in self.SKIP_TAGS:
            self.in_skip += 1
            return
        if tag == "main":
            self.in_main += 1
        if self.in_main == 0 and tag == "article":
            self.in_main = 1
        if self.in_skip == 0 and self.in_main > 0 and tag in self.HEADING_TAGS:
            self.flush_section()
            self.heading_buffer = []
        if self.in_skip == 0 and self.in_main > 0 and tag in self.BLOCK_TAGS:
            self.current_lines.append("\n")

    def handle_endtag(self, tag: str) -> None:
        if tag in self.SKIP_TAGS and self.in_skip > 0:
            self.in_skip -= 1
            return
        if self.in_skip > 0 or self.in_main == 0:
            return
        if tag in self.HEADING_TAGS:
            heading = " ".join(part.strip() for part in self.heading_buffer if part.strip()).strip()
            if heading:
                self.current_heading = heading
        if tag in self.BLOCK_TAGS:
            self.current_lines.append("\n")
        if tag == "main" and self.in_main > 0:
            self.in_main -= 1

    def handle_data(self, data: str) -> None:
        if self.in_skip > 0 or self.in_main == 0:
            return
        value = html.unescape(data)
        if not value.strip():
            return
        self.current_lines.append(value.strip() + " ")
        if self.get_starttag_text() and re.match(r"<h[1-4]", self.get_starttag_text() or ""):
            self.heading_buffer.append(value.strip())

    def flush_section(self) -> None:
        content = re.sub(r"\n{3,}", "\n\n", "".join(self.current_lines)).strip()
        content = re.sub(r"[ \t]+", " ", content)
        content = re.sub(r" ?\n ?", "\n", content)
        if content:
            self.sections.append((self.current_heading, content))
        self.current_lines = []

    def finalize(self) -> list[tuple[str, str]]:
        self.flush_section()
        return self.sections


def cleanup_text(text: str) -> str:
    cleaned = html.unescape(text)
    cleaned = cleaned.replace("\xa0", " ")
    cleaned = re.sub(r"<a\s+href=\"[^\"]*\">.*?</a>", " ", cleaned, flags=re.IGNORECASE | re.DOTALL)
    cleaned = re.sub(r"\bLink copied to clipboard\b", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"\bMembers\b", " ", cleaned)
    cleaned = re.sub(r"\bnative Ptr\b", "nativePtr", cleaned)
    cleaned = re.sub(
        r"Use of\s+`?MapView`?\s+requires\s+a\s+MapLibre\s+API\s+access\s+token\.?",
        " ",
        cleaned,
        flags=re.IGNORECASE,
    )
    cleaned = re.sub(
        r"Obtain an access token on the\s+MapLibre account page\s*\([^)]*mapbox\.com[^)]*\)\.?",
        " ",
        cleaned,
        flags=re.IGNORECASE,
    )
    cleaned = re.sub(r"[^.!?\n]*access token[^.!?\n]*[.!?]?", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"[^.!?\n]*account page[^.!?\n]*[.!?]?", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"https?://[^\s)]*mapbox\.com[^\s)]*", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"See also:\s*MapLibre Style Spec(?:ification)?[^\n]*", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"MapLibre Style Spec(?:ification)?\s*-\s*Symbol Layer", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"https?://maplibre\.org/maplibre-style-spec/[^\s)]*", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"#{1,4}\s*See also\s*\n+", " ", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"[ \t]+", " ", cleaned)
    cleaned = re.sub(r" ?\n ?", "\n", cleaned)
    cleaned = re.sub(r"\n{3,}", "\n\n", cleaned)
    return cleaned.strip()


def strip_html_fragment(fragment: str) -> str:
    fragment = re.sub(r"<wbr\s*/?>", "", fragment, flags=re.IGNORECASE)
    fragment = re.sub(r"<br ?/?>", "\n", fragment, flags=re.IGNORECASE)
    fragment = re.sub(r"</(p|div|li|tr|table|section|pre|code|hr|h1|h2|h3|h4|td|th|ul|ol)>", "\n", fragment, flags=re.IGNORECASE)
    fragment = re.sub(r"<[^>]+>", " ", fragment)
    return cleanup_text(fragment)


def extract_table_items(fragment: str) -> list[tuple[str, str]]:
    rows = re.findall(r'<div class="table-row".*?<div class="main-subrow keyValue ">(.*?)</div></div></div>', fragment, flags=re.IGNORECASE | re.DOTALL)
    items: list[tuple[str, str]] = []
    for row in rows:
        name_match = re.search(r"<u><span><span>(.*?)</span></span></u>|<div><span><span>(.*?)</span></span></div>", row, flags=re.IGNORECASE | re.DOTALL)
        value_match = re.search(r'<div class="title">(.*?)</div>', row, flags=re.IGNORECASE | re.DOTALL)
        name = strip_html_fragment((name_match.group(1) if name_match and name_match.group(1) else name_match.group(2)) if name_match else "")
        value = strip_html_fragment(value_match.group(1) if value_match else "")
        if name and value:
            items.append((name, value))
    return items


def extract_member_page_text(main_content_html: str, max_chars: int) -> str:
    title_match = re.search(r"<h1[^>]*>(.*?)</h1>", main_content_html, re.IGNORECASE | re.DOTALL)
    symbol_match = re.search(r'<div class="symbol monospace">(.*?)</div>', main_content_html, re.IGNORECASE | re.DOTALL)
    paragraph_match = re.search(r'<p class="paragraph">(.*?)</p>', main_content_html, re.IGNORECASE | re.DOTALL)
    parameters_match = re.search(r"<h4[^>]*>\s*Parameters\s*</h4>(.*?)</div></div>", main_content_html, re.IGNORECASE | re.DOTALL)

    chunks: list[str] = []
    title = strip_html_fragment(title_match.group(1) if title_match else "")
    signature = strip_html_fragment(symbol_match.group(1) if symbol_match else "")
    summary = strip_html_fragment(paragraph_match.group(1) if paragraph_match else "")
    if title:
        chunks.append(f"# {title}")
    if signature:
        chunks.append(signature)
    if summary:
        chunks.append(summary)

    parameters = extract_table_items(parameters_match.group(1)) if parameters_match else []
    if parameters:
        chunks.append("Parameters:")
        chunks.extend(f"- {name}: {value}" for name, value in parameters)

    return cleanup_text("\n\n".join(chunks))[:max_chars]


def extract_main_content_html(raw_html: str) -> str:
    start = raw_html.find('<div class="main-content"')
    if start == -1:
        start = raw_html.find('<div id="main">')
    end = raw_html.find('<div class="footer">', start if start != -1 else 0)
    snippet = raw_html[start:end] if start != -1 and end != -1 else raw_html
    snippet = re.sub(r'<div class="breadcrumbs">.*?</div>', " ", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r'<span class="anchor-wrapper">.*?</span>', " ", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r'<div class="copy-popup-wrapper.*?</div>', " ", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<script.*?</script>", " ", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<style.*?</style>", " ", snippet, flags=re.IGNORECASE | re.DOTALL)
    return snippet


def extract_generic_page_text(main_content_html: str) -> str:
    snippet = re.sub(r"<h1[^>]*>(.*?)</h1>", r"\n# \1\n", main_content_html, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<h2[^>]*>(.*?)</h2>", r"\n## \1\n", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<h3[^>]*>(.*?)</h3>", r"\n### \1\n", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<h4[^>]*>(.*?)</h4>", r"\n#### \1\n", snippet, flags=re.IGNORECASE | re.DOTALL)
    return strip_html_fragment(snippet)


def select_sections(sections: list[tuple[str, str]], section_hint: str, max_chars: int) -> tuple[str, str]:
    if not sections:
        return "Introduction", ""

    normalized_hint = normalize(section_hint)
    if normalized_hint:
        for heading, content in sections:
            haystack = normalize(heading + " " + content[:1500])
            if normalized_hint in haystack:
                return heading, content[:max_chars]

    heading, content = sections[0]
    if len(content) >= max_chars:
        return heading, content[:max_chars]

    chunks = [content]
    for next_heading, next_content in sections[1:]:
        candidate = "\n\n".join(chunks + [f"## {next_heading}\n{next_content}"])
        if len(candidate) > max_chars:
            break
        chunks.append(f"## {next_heading}\n{next_content}")
    return heading, "\n\n".join(chunks)[:max_chars]


def fetch_html_page(url: str, section_hint: str, max_chars: int) -> str:
    raw_html = fetch_text(url)
    resolved_url = maybe_resolve_member_fetch_url(url, section_hint, raw_html)
    if resolved_url != url:
        return fetch_html_page(resolved_url, section_hint, max_chars)

    title_match = re.search(r"<title>(.*?)</title>", raw_html, re.IGNORECASE | re.DOTALL)
    title = html.unescape(title_match.group(1).strip()) if title_match else url
    main_content_html = extract_main_content_html(raw_html)
    page_type_match = re.search(r'data-page-type="([^"]+)"', main_content_html, re.IGNORECASE)
    page_type = page_type_match.group(1).strip().lower() if page_type_match else ""

    if page_type == "member":
        text = extract_member_page_text(main_content_html, max_chars * 2)
    else:
        text = extract_generic_page_text(main_content_html)

    sections = extract_heading_sections_from_text(text)
    heading, content = select_sections(sections, section_hint, max_chars)
    if not content or len(content.strip()) < 200:
        content = text[:max_chars]
    return f"Title: {title}\nURL: {url}\nSection: {heading}\n\n{content}".strip()


def handle_search(arguments: dict) -> dict:
    query = str(arguments.get("query", "")).strip()
    if not query:
        return tool_text("`query` is required.", is_error=True)

    limit = max(1, min(int(arguments.get("limit", MAX_RESULTS)), MAX_RESULTS))
    results = symbol_search(query, limit)
    return tool_text(format_results(results))


def handle_fetch(arguments: dict) -> dict:
    url = str(arguments.get("url", "")).strip()
    if not valid_maplibre_url(url):
        return tool_text(
            "Only `https://maplibre.org/maplibre-native/android/api/` URLs are allowed.",
            is_error=True,
        )

    section_hint = str(arguments.get("section_hint", "")).strip()
    max_chars = max(1000, min(int(arguments.get("max_chars", 5000)), 12000))
    return tool_text(fetch_html_page(url, section_hint, max_chars))


TOOLS = [
    {
        "name": "maplibre_android_docs_search",
        "description": "Search MapLibre Android API docs first and return the most relevant maplibre.org Android API pages.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "query": {"type": "string", "description": "Android API class, method, package, or layer/source query."},
                "limit": {"type": "integer", "minimum": 1, "maximum": 5},
            },
            "required": ["query"],
        },
    },
    {
        "name": "maplibre_android_docs_fetch",
        "description": "Fetch only the needed section from a MapLibre Android API page after search selected the page.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "url": {"type": "string", "description": "A maplibre.org MapLibre Android API URL returned by search."},
                "section_hint": {"type": "string", "description": "Optional heading, symbol, fragment, or phrase to narrow the fetch."},
                "max_chars": {"type": "integer", "minimum": 1000, "maximum": 12000},
            },
            "required": ["url"],
        },
    },
]


def handle_request(message: dict) -> dict | None:
    method = message.get("method")
    message_id = message.get("id")

    if method == "initialize":
        return {
            "jsonrpc": "2.0",
            "id": message_id,
            "result": {
                "protocolVersion": PROTOCOL_VERSION,
                "capabilities": {"tools": {"listChanged": False}},
                "serverInfo": {
                    "name": "maplibre-android-docs",
                    "version": "1.0.0",
                },
                "instructions": (
                    "Use search first, then fetch only the smallest needed section. "
                    "If search returns an exact member page for a method or property, prefer that page over the class page. "
                    "Avoid broad class-page fetches when an exact member page exists. "
                    "Restrict retrieval to MapLibre Android API pages under maplibre.org/maplibre-native/android/api/. "
                    "Answer only from the fetched section. Do not add access-token requirements, Style Spec references, or other external-source details unless they are explicitly present in the fetched text."
                ),
            },
        }

    if method == "notifications/initialized":
        return None

    if method == "ping":
        return {"jsonrpc": "2.0", "id": message_id, "result": {}}

    if method == "tools/list":
        return {"jsonrpc": "2.0", "id": message_id, "result": {"tools": TOOLS}}

    if method == "tools/call":
        params = message.get("params", {})
        name = params.get("name")
        arguments = params.get("arguments", {})

        try:
            if name == "maplibre_android_docs_search":
                result = handle_search(arguments)
            elif name == "maplibre_android_docs_fetch":
                result = handle_fetch(arguments)
            else:
                return {
                    "jsonrpc": "2.0",
                    "id": message_id,
                    "error": {"code": -32602, "message": f"Unknown tool: {name}"},
                }
        except Exception as error:
            result = tool_text(f"Tool execution failed: {error}", is_error=True)

        return {"jsonrpc": "2.0", "id": message_id, "result": result}

    if message_id is not None:
        return {
            "jsonrpc": "2.0",
            "id": message_id,
            "error": {"code": -32601, "message": f"Method not found: {method}"},
        }
    return None


def main() -> int:
    for line in sys.stdin:
        raw = line.strip()
        if not raw:
            continue
        try:
            message = json.loads(raw)
        except json.JSONDecodeError as error:
            write_message({
                "jsonrpc": "2.0",
                "error": {"code": -32700, "message": f"Parse error: {error}"},
            })
            continue

        response = handle_request(message)
        if response is not None:
            write_message(response)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
