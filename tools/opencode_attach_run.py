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


def build_client_command(opencode: str, attach_url: str, argv: list[str]) -> list[str]:
    cwd = os.getcwd()
    if argv and argv[0] == "--shell":
        return [opencode, "attach", attach_url, "--dir", cwd, *argv[1:]]
    return [opencode, "run", "--attach", attach_url, "--dir", cwd, *argv]


def run_serve_only(opencode: str, port: int, attach_url: str) -> int:
    serve_command = [opencode, "serve", "--port", str(port)]
    serve_process = subprocess.Popen(
        serve_command,
        text=True,
        env=os.environ.copy(),
    )

    try:
        wait_for_server(attach_url, STARTUP_TIMEOUT_SECONDS)
        print(f"OpenCode server ready: {attach_url}")
        print(f"Attach with: {opencode} attach {attach_url} --dir {os.getcwd()}")
        print("Press Ctrl+C to stop the server.")
        return serve_process.wait()
    except KeyboardInterrupt:
        return 130
    finally:
        if serve_process.poll() is None:
            serve_process.terminate()
            try:
                serve_process.wait(timeout=5)
            except subprocess.TimeoutExpired:
                serve_process.kill()
                serve_process.wait(timeout=5)


def main(argv: list[str]) -> int:
    if not argv:
        print(
            "Usage: python tools/opencode_attach_run.py [opencode run args...]\n"
            "   or: python tools/opencode_attach_run.py --shell [opencode attach args...]\n"
            "   or: python tools/opencode_attach_run.py --serve-only",
            file=sys.stderr,
        )
        return 2

    opencode = resolve_opencode()
    port = reserve_port()
    attach_url = f"http://127.0.0.1:{port}"

    if argv == ["--serve-only"]:
        return run_serve_only(opencode, port, attach_url)

    serve_command = [opencode, "serve", "--port", str(port)]
    client_command = build_client_command(opencode, attach_url, argv)

    serve_process = subprocess.Popen(
        serve_command,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
        text=True,
        env=os.environ.copy(),
    )

    try:
        wait_for_server(attach_url, STARTUP_TIMEOUT_SECONDS)
        completed = subprocess.run(client_command, env=os.environ.copy())
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
