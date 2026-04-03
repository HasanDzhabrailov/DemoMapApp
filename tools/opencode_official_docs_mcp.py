#!/usr/bin/env python3

import html
import json
import re
import sys
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from html.parser import HTMLParser
from typing import Iterable


USER_AGENT = "DemoMapApp-OpenCode-MCP/1.0"
PROTOCOL_VERSION = "2025-03-26"
KOTLIN_LLM_INDEX = "https://kotlinlang.org/llms.txt"
KOTLIN_SITEMAP = "https://kotlinlang.org/sitemap.xml"
MAX_RESULTS = 5
REQUEST_TIMEOUT_SECONDS = 30


@dataclass
class SearchEntry:
    title: str
    url: str
    kind: str
    source: str
    score: int


_CACHE: dict[str, object] = {}

ANDROID_GUIDE_CATALOG = [
    ("ViewModel overview", "https://developer.android.com/topic/libraries/architecture/viewmodel"),
    ("UI state in Compose", "https://developer.android.com/develop/ui/compose/state"),
    ("Compose side-effects", "https://developer.android.com/develop/ui/compose/side-effects"),
    ("Compose state hoisting", "https://developer.android.com/develop/ui/compose/state-hoisting"),
    ("Compose lifecycle", "https://developer.android.com/develop/ui/compose/lifecycle"),
    ("Navigation overview", "https://developer.android.com/guide/navigation"),
    ("Navigation principles", "https://developer.android.com/guide/navigation/navigation-principles"),
    ("App architecture", "https://developer.android.com/topic/architecture"),
    ("Guide to app architecture", "https://developer.android.com/topic/architecture/intro"),
    ("Modularization", "https://developer.android.com/topic/modularization"),
    ("Testing fundamentals", "https://developer.android.com/training/testing/fundamentals"),
    ("Request app permissions", "https://developer.android.com/training/permissions/requesting"),
    ("Activities introduction", "https://developer.android.com/guide/components/activities/intro-activities"),
    ("Fragments overview", "https://developer.android.com/guide/fragments"),
    ("Save UI states", "https://developer.android.com/topic/libraries/architecture/saving-states"),
    ("WorkManager overview", "https://developer.android.com/topic/libraries/architecture/workmanager"),
    ("Large screens overview", "https://developer.android.com/guide/topics/large-screens/get-started-with-large-screens"),
    ("Background work overview", "https://developer.android.com/develop/background-work"),
    ("Location overview", "https://developer.android.com/develop/sensors-and-location/location"),
    ("Coroutines on Android", "https://developer.android.com/kotlin/coroutines"),
]

ANDROID_API_CATALOG = [
    ("androidx.compose.runtime package", "https://developer.android.com/reference/kotlin/androidx/compose/runtime/package-summary"),
    ("androidx.compose.ui package", "https://developer.android.com/reference/kotlin/androidx/compose/ui/package-summary"),
    ("androidx.compose.foundation package", "https://developer.android.com/reference/kotlin/androidx/compose/foundation/package-summary"),
    ("androidx.lifecycle kotlin package", "https://developer.android.com/reference/kotlin/androidx/lifecycle/package-summary"),
    ("androidx.lifecycle java package", "https://developer.android.com/reference/androidx/lifecycle/package-summary"),
    ("androidx.navigation kotlin package", "https://developer.android.com/reference/kotlin/androidx/navigation/package-summary"),
    ("androidx.navigation.compose package", "https://developer.android.com/reference/kotlin/androidx/navigation/compose/package-summary"),
    ("androidx.work kotlin package", "https://developer.android.com/reference/kotlin/androidx/work/package-summary"),
    ("androidx.activity.compose package", "https://developer.android.com/reference/kotlin/androidx/activity/compose/package-summary"),
]


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


def infer_intent(query: str, intent: str) -> str:
    if intent in {"api", "guide"}:
        return intent
    api_patterns = [
        r"[A-Z][A-Za-z0-9]+",
        r"[a-z0-9_]+\([^)]+\)",
        r"\b(androidx|android\.|kotlin\.|java\.)",
        r"\b(class|interface|function|method|property|annotation|package|api)\b",
    ]
    lowered = query.lower()
    if any(re.search(pattern, query) for pattern in api_patterns):
        return "api"
    if any(keyword in lowered for keyword in ["lifecycle", "guide", "workflow", "overview", "how to", "when to"]):
        return "guide"
    return "guide"


def valid_official_url(url: str) -> bool:
    parsed = urllib.parse.urlparse(url)
    return parsed.scheme in {"https", "http"} and parsed.netloc in {
        "developer.android.com",
        "kotlinlang.org",
    }


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
        attrs_map = dict(attrs)
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
        href = attrs_map.get("href") or ""
        if tag == "a" and href.startswith("#"):
            self.current_lines.append(" ")

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


def select_sections(sections: list[tuple[str, str]], section_hint: str, max_chars: int) -> tuple[str, str]:
    if not sections:
        return "Introduction", ""

    normalized_hint = normalize(section_hint)
    if normalized_hint:
        for heading, content in sections:
            haystack = normalize(heading + " " + content[:1500])
            if normalized_hint and normalized_hint in haystack:
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


def format_results(results: list[SearchEntry]) -> str:
    if not results:
        return "No official documentation matches were found. Refine the query and try again."

    lines = []
    for index, result in enumerate(results, start=1):
        lines.append(
            f"{index}. [{result.kind}] {result.title}\n"
            f"   URL: {result.url}\n"
            f"   Source: {result.source}\n"
            f"   Score: {result.score}"
        )
    return "\n\n".join(lines)


def url_exists(url: str) -> bool:
    cache_key = f"exists:{url}"
    if cache_key in _CACHE:
        return bool(_CACHE[cache_key])

    try:
        request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
        with urllib.request.urlopen(request, timeout=10) as response:
            exists = 200 <= response.status < 400
    except Exception:
        exists = False

    _CACHE[cache_key] = exists
    return exists


def page_contains(url: str, term: str) -> bool:
    normalized_term = term.strip().lower()
    if not normalized_term:
        return False

    cache_key = f"contains:{url}:{normalized_term}"
    if cache_key in _CACHE:
        return bool(_CACHE[cache_key])

    try:
        text = fetch_text(url).lower()
        contains = normalized_term in text
    except Exception:
        contains = False

    _CACHE[cache_key] = contains
    return contains


def page_symbol_bonus(url: str, term: str) -> int:
    normalized_term = term.strip()
    if not normalized_term:
        return 0

    cache_key = f"symbol-bonus:{url}:{normalized_term.lower()}"
    if cache_key in _CACHE:
        cached_value = _CACHE[cache_key]
        return cached_value if isinstance(cached_value, int) else 0

    try:
        text = fetch_text(url)
    except Exception:
        _CACHE[cache_key] = 0
        return 0

    lowered = text.lower()
    term_lower = normalized_term.lower()
    bonus = 0

    if re.search(rf"\b{re.escape(term_lower)}\s*\(", lowered):
        bonus = max(bonus, 80)
    if re.search(rf"data-text=\"{re.escape(normalized_term)}\"", text):
        bonus = max(bonus, 80)
    if re.search(rf"id=\"{re.escape(term_lower)}\"", lowered):
        bonus = max(bonus, 70)
    if term_lower in lowered:
        bonus = max(bonus, 40)

    _CACHE[cache_key] = bonus
    return bonus


def extract_api_symbol(query: str) -> str:
    raw_terms = re.findall(r"[A-Za-z0-9_.]+", query)
    for term in reversed(raw_terms):
        if "." in term:
            return term.split(".")[-1]
        if re.match(r"[A-Z][A-Za-z0-9]+$", term):
            return term
        if re.match(r"[a-z]+[A-Z][A-Za-z0-9]+$", term):
            return term
    return ""


def android_api_candidates(query: str) -> list[tuple[str, str]]:
    candidates: list[tuple[str, str]] = []
    raw_terms = re.findall(r"[A-Za-z0-9_.]+", query)
    dotted = [term for term in raw_terms if "." in term and not term.endswith(".")]

    for term in dotted:
        path = term.replace(".", "/")
        candidates.append((term, f"https://developer.android.com/reference/{path}"))
        package_path = "/".join(term.split(".")[:-1])
        if package_path:
            candidates.append((f"{package_path} package", f"https://developer.android.com/reference/{package_path}/package-summary"))
            candidates.append((f"{package_path} kotlin package", f"https://developer.android.com/reference/kotlin/{package_path}/package-summary"))

    package_parts = [token.lower() for token in raw_terms if token and token[0].islower()]
    symbol_parts = [token for token in raw_terms if token and token[0].isupper()]
    if package_parts and symbol_parts:
        symbol = symbol_parts[-1]
        package_path = "/".join(package_parts)
        label = ".".join(package_parts + [symbol])
        candidates.append((label, f"https://developer.android.com/reference/{package_path}/{symbol}"))
        candidates.append((f"{package_path} package", f"https://developer.android.com/reference/{package_path}/package-summary"))
        candidates.append((f"{package_path} kotlin package", f"https://developer.android.com/reference/kotlin/{package_path}/package-summary"))

    normalized_query = normalize(query)
    if "compose runtime" in normalized_query:
        candidates.append(("androidx.compose.runtime package", "https://developer.android.com/reference/kotlin/androidx/compose/runtime/package-summary"))
    if "compose" in normalized_query:
        candidates.append(("androidx.compose.runtime package", "https://developer.android.com/reference/kotlin/androidx/compose/runtime/package-summary"))
        candidates.append(("androidx.compose.ui package", "https://developer.android.com/reference/kotlin/androidx/compose/ui/package-summary"))
    if "viewmodel" in normalized_query:
        candidates.append(("androidx.lifecycle.ViewModel", "https://developer.android.com/reference/androidx/lifecycle/ViewModel"))
    if "lifecycle" in normalized_query:
        candidates.append(("androidx.lifecycle kotlin package", "https://developer.android.com/reference/kotlin/androidx/lifecycle/package-summary"))
        candidates.append(("androidx.lifecycle java package", "https://developer.android.com/reference/androidx/lifecycle/package-summary"))
    if "navigation" in normalized_query:
        candidates.append(("androidx.navigation kotlin package", "https://developer.android.com/reference/kotlin/androidx/navigation/package-summary"))
    if "coroutine" in normalized_query:
        candidates.append(("Coroutines on Android", "https://developer.android.com/kotlin/coroutines"))

    symbol = extract_api_symbol(query)
    if symbol and not dotted:
        candidates.extend(ANDROID_API_CATALOG)

    deduped: list[tuple[str, str]] = []
    seen: set[str] = set()
    for title, url in candidates:
        if url in seen:
            continue
        seen.add(url)
        deduped.append((title, url))
    return deduped


def load_kotlin_llms_entries() -> list[SearchEntry]:
    if "kotlin_llms" in _CACHE:
        return _CACHE["kotlin_llms"]  # type: ignore[return-value]

    raw = fetch_text(KOTLIN_LLM_INDEX)
    entries: list[SearchEntry] = []
    for line in raw.splitlines():
        match = re.match(r"^- \[(.+?)\]\((https://kotlinlang\.org/[^)]+)\)", line.strip())
        if not match:
            continue
        title, url = match.groups()
        kind = "api" if "/api/" in url else "guide"
        entries.append(SearchEntry(title=title, url=url, kind=kind, source="kotlin-llms", score=0))

    _CACHE["kotlin_llms"] = entries
    return entries


def load_kotlin_api_entries() -> list[SearchEntry]:
    if "kotlin_api" in _CACHE:
        return _CACHE["kotlin_api"]  # type: ignore[return-value]

    sitemap = fetch_text(KOTLIN_SITEMAP)
    entries: list[SearchEntry] = []
    for url in re.findall(r"<loc>(https://kotlinlang\.org/[^<]+)</loc>", sitemap):
        if "/api/" not in url:
            continue
        parsed = urllib.parse.urlparse(url)
        title = parsed.path.rstrip("/").split("/")[-1] or parsed.netloc
        entries.append(SearchEntry(title=title, url=url, kind="api", source="kotlin-sitemap", score=0))

    _CACHE["kotlin_api"] = entries
    return entries


def score_entry(query: str, tokens: list[str], title: str, url: str, preferred_kind: str, actual_kind: str) -> int:
    haystacks = [normalize(title), normalize(url), normalize(urllib.parse.urlparse(url).path)]
    score = 0

    if preferred_kind == actual_kind:
        score += 15
    elif preferred_kind == "guide" and actual_kind != "api":
        score += 10

    collapsed_query = normalize(query)
    if collapsed_query and collapsed_query in haystacks[0]:
        score += 30
    if collapsed_query and collapsed_query in haystacks[1]:
        score += 20

    for token in tokens:
        for haystack in haystacks:
            if token == haystack:
                score += 10
            elif token in haystack:
                score += 4

    if "/reference/" in url and preferred_kind == "api":
        score += 8
    if "/_llms/" in url and preferred_kind == "guide":
        score += 8

    return score


def android_search(query: str, intent: str, limit: int) -> list[SearchEntry]:
    tokens = tokenize(query)
    preferred_kind = infer_intent(query, intent)
    results: list[SearchEntry] = []
    api_symbol = extract_api_symbol(query)

    if preferred_kind in {"api", "guide"}:
        search_kinds = [preferred_kind]
    else:
        search_kinds = ["api", "guide"]

    if "api" in search_kinds:
        for title, url in android_api_candidates(query):
            if not url_exists(url):
                continue
            score = score_entry(query, tokens, title, url, preferred_kind, "api")
            if api_symbol:
                score += page_symbol_bonus(url, api_symbol)
            if score <= 0:
                continue
            if score >= 30:
                results.append(SearchEntry(title=title, url=url, kind="api", source="android-direct", score=score))

    if "guide" in search_kinds:
        for title, url in ANDROID_GUIDE_CATALOG:
            score = score_entry(query, tokens, title, url, preferred_kind, "guide")
            if score <= 0:
                continue
            if score >= 24:
                results.append(SearchEntry(title=title, url=url, kind="guide", source="android-guide-catalog", score=score))

    results.sort(key=lambda item: (-item.score, item.url))
    return results[:limit]


def kotlin_search(query: str, intent: str, limit: int) -> list[SearchEntry]:
    tokens = tokenize(query)
    preferred_kind = infer_intent(query, intent)
    pool = list(load_kotlin_llms_entries())
    if preferred_kind == "api":
        pool.extend(load_kotlin_api_entries())

    scored: list[SearchEntry] = []
    for entry in pool:
        score = score_entry(query, tokens, entry.title, entry.url, preferred_kind, entry.kind)
        if score <= 0:
            continue
        scored.append(SearchEntry(entry.title, entry.url, entry.kind, entry.source, score))

    deduped: dict[str, SearchEntry] = {}
    for entry in sorted(scored, key=lambda item: (-item.score, item.url)):
        deduped.setdefault(entry.url, entry)
    return list(deduped.values())[:limit]


def fetch_plain_text_page(url: str, section_hint: str, max_chars: int) -> str:
    text = fetch_text(url)
    sections = extract_heading_sections_from_text(text)
    heading, content = select_sections(sections, section_hint, max_chars)
    return f"URL: {url}\nSection: {heading}\n\n{content}".strip()


def fetch_android_html_page(url: str, section_hint: str, max_chars: int) -> str:
    raw_html = fetch_text(url)
    title_match = re.search(r"<title>(.*?)</title>", raw_html, re.IGNORECASE | re.DOTALL)
    title = html.unescape(title_match.group(1).strip()) if title_match else url

    start = raw_html.find('devsite-article-body')
    if start == -1:
        start = raw_html.find('<div id="header-block">')
    end_markers = ['<devsite-content-footer', '<footer class="devsite-footer"', '<devsite-reference-nav']
    end = len(raw_html)
    for marker in end_markers:
        position = raw_html.find(marker, start)
        if position != -1:
            end = min(end, position)

    snippet = raw_html[start:end]
    snippet = re.sub(r"<script.*?</script>", " ", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<style.*?</style>", " ", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<h1[^>]*>(.*?)</h1>", r"\n# \1\n", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<h2[^>]*>(.*?)</h2>", r"\n## \1\n", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<h3[^>]*>(.*?)</h3>", r"\n### \1\n", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"<h4[^>]*>(.*?)</h4>", r"\n#### \1\n", snippet, flags=re.IGNORECASE | re.DOTALL)
    snippet = re.sub(r"</(p|div|li|tr|table|section|pre|code|hr)>", "\n", snippet, flags=re.IGNORECASE)
    snippet = re.sub(r"<br ?/?>", "\n", snippet, flags=re.IGNORECASE)
    snippet = re.sub(r"<[^>]+>", " ", snippet)
    text = html.unescape(snippet)
    text = re.sub(r"\r", "", text)
    text = re.sub(r"[ \t]+", " ", text)
    text = re.sub(r"\n{3,}", "\n\n", text).strip()

    sections = extract_heading_sections_from_text(text)
    heading, content = select_sections(sections, section_hint, max_chars)
    return f"Title: {title}\nURL: {url}\nSection: {heading}\n\n{content}".strip()


def fetch_html_page(url: str, section_hint: str, max_chars: int) -> str:
    if "developer.android.com" in url:
        return fetch_android_html_page(url, section_hint, max_chars)

    raw_html = fetch_text(url)
    parser = SectionHTMLParser()
    parser.feed(raw_html)
    sections = parser.finalize()

    title_match = re.search(r"<title>(.*?)</title>", raw_html, re.IGNORECASE | re.DOTALL)
    title = html.unescape(title_match.group(1).strip()) if title_match else url
    heading, content = select_sections(sections, section_hint, max_chars)
    if not content:
        content = re.sub(r"\s+", " ", html.unescape(re.sub(r"<[^>]+>", " ", raw_html)))[:max_chars]
    return f"Title: {title}\nURL: {url}\nSection: {heading}\n\n{content}".strip()


def handle_search(source: str, arguments: dict) -> dict:
    query = str(arguments.get("query", "")).strip()
    if not query:
        return tool_text("`query` is required.", is_error=True)

    intent = str(arguments.get("intent", "auto")).strip().lower() or "auto"
    limit = max(1, min(int(arguments.get("limit", MAX_RESULTS)), MAX_RESULTS))

    if source == "android":
        results = android_search(query, intent, limit)
    else:
        results = kotlin_search(query, intent, limit)

    return tool_text(format_results(results))


def handle_fetch(arguments: dict) -> dict:
    url = str(arguments.get("url", "")).strip()
    if not valid_official_url(url):
        return tool_text("Only `developer.android.com` and `kotlinlang.org` URLs are allowed.", is_error=True)

    section_hint = str(arguments.get("section_hint", "")).strip()
    max_chars = max(1000, min(int(arguments.get("max_chars", 5000)), 12000))

    if "/_llms/" in url:
        text = fetch_plain_text_page(url, section_hint, max_chars)
    else:
        text = fetch_html_page(url, section_hint, max_chars)

    return tool_text(text)


TOOLS = [
    {
        "name": "android_docs_search",
        "description": "Search official Android docs first and return the most relevant developer.android.com pages.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "query": {"type": "string", "description": "API, class, package, or guide query."},
                "intent": {
                    "type": "string",
                    "enum": ["auto", "api", "guide"],
                    "description": "Prefer API reference or guide pages.",
                },
                "limit": {"type": "integer", "minimum": 1, "maximum": 5},
            },
            "required": ["query"],
        },
    },
    {
        "name": "kotlin_docs_search",
        "description": "Search official Kotlin docs first and return the most relevant kotlinlang.org pages.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "query": {"type": "string", "description": "Language feature, stdlib API, or guide query."},
                "intent": {
                    "type": "string",
                    "enum": ["auto", "api", "guide"],
                    "description": "Prefer API reference or guide pages.",
                },
                "limit": {"type": "integer", "minimum": 1, "maximum": 5},
            },
            "required": ["query"],
        },
    },
    {
        "name": "official_docs_fetch",
        "description": "Fetch only the needed section from an official Android or Kotlin documentation page after search selected the page.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "url": {"type": "string", "description": "A developer.android.com or kotlinlang.org URL returned by search."},
                "section_hint": {"type": "string", "description": "Optional heading, fragment, or phrase to narrow the fetch."},
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
                    "name": "official-docs",
                    "version": "1.0.0",
                },
                "instructions": (
                    "Use search first, then fetch only the smallest needed section. "
                    "Prefer API pages for symbols and guide pages for concepts."
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
            if name == "android_docs_search":
                result = handle_search("android", arguments)
            elif name == "kotlin_docs_search":
                result = handle_search("kotlin", arguments)
            elif name == "official_docs_fetch":
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
