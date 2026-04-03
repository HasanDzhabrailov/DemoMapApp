#!/usr/bin/env python3

import os
import shutil
import socket
import subprocess
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path


STARTUP_TIMEOUT_SECONDS = 20


def resolve_opencode() -> str:
    candidates = [
        shutil.which("opencode"),
        str(Path.home() / ".opencode" / "bin" / "opencode"),
        str(Path.home() / ".opencode" / "bin" / "opencode.exe"),
    ]
    for candidate in candidates:
        if candidate and Path(candidate).exists():
            return candidate
    raise FileNotFoundError("opencode executable was not found in PATH or ~/.opencode/bin")


def reserve_port() -> int:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.bind(("127.0.0.1", 0))
        sock.listen(1)
        return sock.getsockname()[1]


def wait_for_server(url: str, timeout_seconds: int) -> None:
    deadline = time.time() + timeout_seconds
    while time.time() < deadline:
        try:
            urllib.request.urlopen(url, timeout=2)
            return
        except urllib.error.HTTPError as error:
            if error.code in {200, 401, 404}:
                return
        except Exception:
            time.sleep(0.2)
            continue
    raise TimeoutError(f"Timed out waiting for OpenCode server at {url}")


def main(argv: list[str]) -> int:
    if not argv:
        print("Usage: python tools/opencode_attach_run.py [opencode run args...]", file=sys.stderr)
        return 2

    opencode = resolve_opencode()
    port = reserve_port()
    attach_url = f"http://127.0.0.1:{port}"

    serve_command = [opencode, "serve", "--port", str(port)]
    run_command = [opencode, "run", "--attach", attach_url, *argv]

    serve_process = subprocess.Popen(
        serve_command,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
        text=True,
        env=os.environ.copy(),
    )

    try:
        wait_for_server(attach_url, STARTUP_TIMEOUT_SECONDS)
        completed = subprocess.run(run_command, env=os.environ.copy())
        return completed.returncode
    finally:
        serve_process.terminate()
        try:
            serve_process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            serve_process.kill()
            serve_process.wait(timeout=5)


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
