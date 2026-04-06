#!/usr/bin/env python3

import json
import subprocess
import sys


SERVER_COMMAND = ["python", "tools/opencode_maplibre_android_docs_mcp.py"]
CASES = [
    {
        "query": "MapView",
        "expected_search_url": "-map-view/index.html",
        "fetch_url": "https://maplibre.org/maplibre-native/android/api/-map-libre%20-native%20-android/org.maplibre.android.maps/-map-view/index.html",
        "section_hint": "constructor",
        "expected_fetch_phrase": "constructor",
        "forbidden_fetch_phrases": ["MapLibre API access token", "mapbox.com"],
    },
    {
        "query": "Style.addSource",
        "expected_search_url": "-style/add-source.html",
        "fetch_url": "https://maplibre.org/maplibre-native/android/api/-map-libre%20-native%20-android/org.maplibre.android.maps/-style/add-source.html",
        "section_hint": "addSource",
        "expected_fetch_phrase": "Adds the source to the map",
    },
    {
        "query": "GeoJsonSource",
        "expected_search_url": "-geo-json-source/index.html",
        "fetch_url": "https://maplibre.org/maplibre-native/android/api/-map-libre%20-native%20-android/org.maplibre.android.style.sources/-geo-json-source/index.html",
        "section_hint": "GeoJsonSource",
        "expected_fetch_phrase": "GeoJson source",
    },
    {
        "query": "SymbolLayer",
        "expected_search_url": "-symbol-layer/index.html",
        "fetch_url": "https://maplibre.org/maplibre-native/android/api/-map-libre%20-native%20-android/org.maplibre.android.style.layers/-symbol-layer/index.html",
        "section_hint": "constructor",
        "expected_fetch_phrase": "Creates a SymbolLayer",
        "forbidden_fetch_phrases": ["maplibre-style-spec", "Style Spec"],
    },
]


def rpc(process: subprocess.Popen[str], message: dict) -> dict:
    assert process.stdin is not None
    assert process.stdout is not None
    process.stdin.write(json.dumps(message) + "\n")
    process.stdin.flush()
    return json.loads(process.stdout.readline())


def require(condition: bool, message: str) -> None:
    if not condition:
        raise AssertionError(message)


def main() -> int:
    process = subprocess.Popen(SERVER_COMMAND, stdin=subprocess.PIPE, stdout=subprocess.PIPE, text=True)
    try:
        rpc(process, {"jsonrpc": "2.0", "id": 1, "method": "initialize", "params": {}})

        for index, case in enumerate(CASES, start=2):
            search_response = rpc(
                process,
                {
                    "jsonrpc": "2.0",
                    "id": index * 10,
                    "method": "tools/call",
                    "params": {
                        "name": "maplibre_android_docs_search",
                        "arguments": {"query": case["query"], "limit": 3},
                    },
                },
            )
            search_text = search_response["result"]["content"][0]["text"]
            require(case["expected_search_url"] in search_text, f"search failed for {case['query']}")

            fetch_response = rpc(
                process,
                {
                    "jsonrpc": "2.0",
                    "id": index * 10 + 1,
                    "method": "tools/call",
                    "params": {
                        "name": "maplibre_android_docs_fetch",
                        "arguments": {
                            "url": case["fetch_url"],
                            "section_hint": case["section_hint"],
                            "max_chars": 1800,
                        },
                    },
                },
            )
            fetch_text = fetch_response["result"]["content"][0]["text"]
            require(case["expected_fetch_phrase"] in fetch_text, f"fetch failed for {case['query']}")
            for forbidden_phrase in case.get("forbidden_fetch_phrases", []):
                require(forbidden_phrase not in fetch_text, f"forbidden phrase present for {case['query']}: {forbidden_phrase}")

        allowlist_response = rpc(
            process,
            {
                "jsonrpc": "2.0",
                "id": 999,
                "method": "tools/call",
                "params": {
                    "name": "maplibre_android_docs_fetch",
                    "arguments": {
                        "url": "https://developer.android.com/reference/android/view/View",
                        "section_hint": "View",
                        "max_chars": 1000,
                    },
                },
            },
        )
        require(allowlist_response["result"]["isError"] is True, "allowlist check failed")
        print("MapLibre MCP smoke checks passed")
        return 0
    finally:
        process.terminate()
        process.wait(timeout=5)


if __name__ == "__main__":
    sys.exit(main())
