#!/usr/bin/env python3
"""Check retained JVM descriptors and overridability, not full binary compatibility."""
import re
import subprocess
import sys
import tempfile
from pathlib import Path
from zipfile import ZipFile


def parse(text):
    classes = {}
    current = None
    declaration = None
    for line in text.splitlines():
        match = re.match(r"public (?:final |abstract )?(class|interface) ([\w.$]+)", line)
        if match:
            current = match[2]
            classes[current] = {"declaration": line, "kind": match[1], "members": {}}
        elif re.match(r"  (public|protected) ", line) and "(" in line:
            declaration = line.strip()
        elif line.strip().startswith("descriptor:") and declaration:
            name = declaration.split("(")[0].split()[-1]
            descriptor = line.split("descriptor:", 1)[1].strip()
            classes[current]["members"][name, descriptor] = declaration
            declaration = None
    return classes


def check(aar):
    baseline = parse((Path(__file__).resolve().parent.parent / "api/java-public-api.txt").read_text())
    with tempfile.TemporaryDirectory() as directory:
        jar = Path(directory) / "classes.jar"
        with ZipFile(aar) as archive:
            jar.write_bytes(archive.read("classes.jar"))
        output = subprocess.check_output(
            ["javap", "-protected", "-s", "-classpath", str(jar), *baseline], text=True
        )
    assert all(c["members"] for c in baseline.values()), "Empty API baseline"
    actual = parse(output)
    failures = []
    count = 0
    for name, expected in baseline.items():
        observed = actual.get(name)
        if not observed:
            failures.append(f"Missing public type: {name}")
            continue
        if expected["kind"] != observed["kind"]:
            failures.append(f"Changed type kind: {name}")
        if " final " not in expected["declaration"] and " final " in observed["declaration"]:
            failures.append(f"Type is no longer extensible: {name}")
        for member, declaration in expected["members"].items():
            count += 1
            found = observed["members"].get(member)
            if not found:
                failures.append(f"Missing member: {name}.{member}")
                continue
            for modifier in ("static", "abstract"):
                if (modifier in declaration.split()) != (modifier in found.split()):
                    failures.append(f"Changed {modifier} modifier: {name}.{member}")
            if "final" not in declaration.split() and "final" in found.split():
                failures.append(f"Member is no longer overridable: {name}.{member}")
    if failures:
        raise SystemExit("\n".join(failures))
    print(f"Retained {count} JVM members in {len(baseline)} public types, including overridability")


if __name__ == "__main__":
    check(Path(sys.argv[1] if len(sys.argv) > 1 else "dialogplus/build/outputs/aar/dialogplus-release.aar"))
