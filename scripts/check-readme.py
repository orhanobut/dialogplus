#!/usr/bin/env python3
"""Validate local README references and keep installation examples tied to a released version."""
import re
from pathlib import Path

root = Path(__file__).resolve().parent.parent
text = (root / "README.md").read_text()
assert text.count("```") % 2 == 0, "Unclosed code fence"
assert 'implementation("com.orhanobut:dialogplus:1.11")' in text
assert "@aar" not in text
for target in re.findall(r"\]\(([^)]+)\)", text) + re.findall(r'src="([^"]+)"', text):
    if not target.startswith(("https://", "#")):
        assert (root / target).exists(), f"Broken local reference: {target}"
assert not re.search(r"travis|jcenter|ossrh|NEXUS_PASSWORD", text, re.I)
print("README: balanced code fences, existing images/license and released installation coordinate")
