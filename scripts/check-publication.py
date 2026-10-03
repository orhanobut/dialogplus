#!/usr/bin/env python3
"""Inspect the locally published AAR, POM, Gradle metadata, sources and API docs."""
import json
import sys
import tomllib
import xml.etree.ElementTree as ET
from pathlib import Path
from zipfile import ZipFile

root = Path(__file__).resolve().parent.parent
repository = Path(sys.argv[1] if len(sys.argv) > 1 else root / "build/maven-local")
version = next(line.split("=", 1)[1] for line in (root / "gradle.properties").read_text().splitlines()
               if line.startswith("VERSION_NAME="))
kotlin = tomllib.loads((root / "gradle/libs.versions.toml").read_text())["versions"]["kotlin"]
base = repository / "com/orhanobut/dialogplus" / version
prefix = f"dialogplus-{version}"
ns = {"m": "http://maven.apache.org/POM/4.0.0"}
pom = ET.parse(base / f"{prefix}.pom").getroot()
for field, expected in (("groupId", "com.orhanobut"), ("artifactId", "dialogplus"),
                        ("version", version), ("packaging", "aar")):
    assert pom.findtext(f"m:{field}", namespaces=ns) == expected, field
for field in ("name", "description", "url", "licenses/m:license/m:name", "developers/m:developer/m:name",
              "scm/m:connection", "scm/m:developerConnection"):
    assert pom.findtext(f"m:{field}", namespaces=ns), field
assert [(d.findtext("m:groupId", namespaces=ns), d.findtext("m:artifactId", namespaces=ns),
         d.findtext("m:version", namespaces=ns), d.findtext("m:scope", namespaces=ns))
        for d in pom.findall("m:dependencies/m:dependency", ns)] == [
    ("org.jetbrains.kotlin", "kotlin-stdlib", kotlin, "compile")]
metadata = json.loads((base / f"{prefix}.module").read_text())
library_variants = [v for v in metadata["variants"] if v["attributes"].get("org.gradle.category") == "library"]
assert {v["attributes"]["org.gradle.usage"] for v in library_variants} == {"java-api", "java-runtime"}
for variant in library_variants:
    assert [(d["group"], d["module"], d["version"]["requires"])
            for d in variant.get("dependencies", [])] == [("org.jetbrains.kotlin", "kotlin-stdlib", kotlin)]
with ZipFile(base / f"{prefix}.aar") as archive:
    manifest = ET.fromstring(archive.read("AndroidManifest.xml"))
    assert manifest.attrib["package"] == "com.orhanobut.dialogplus"
    assert manifest.find("uses-sdk").attrib["{http://schemas.android.com/apk/res/android}minSdkVersion"] == "15"
    assert b"DialogTestActivity" not in archive.read("AndroidManifest.xml")
    aar_metadata = archive.read("META-INF/com/android/build/gradle/aar-metadata.properties").decode()
    assert "minCompileSdk=28" in aar_metadata
with ZipFile(base / f"{prefix}-sources.jar") as archive:
    assert any(name.endswith("DialogPlus.kt") for name in archive.namelist())
    assert not any(name.endswith(".java") or "DialogTestActivity" in name for name in archive.namelist())
with ZipFile(base / f"{prefix}-javadoc.jar") as archive:
    assert any(name.endswith("index.html") for name in archive.namelist())
    assert any("-dialog-plus" in name and name.endswith(".html") for name in archive.namelist())
print("Publication verified: API 15 AAR, Kotlin stdlib metadata, Kotlin sources and generated API documentation")
