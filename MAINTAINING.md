# Maintaining DialogPlus

The working version is `com.orhanobut:dialogplus:2.0.0-SNAPSHOT`. Maven Central's
available release remains 1.11; README examples deliberately use its public APIs.
No remote release is performed by CI.

## Toolchain

Stable versions were verified on 2 October 2026 using official release notes and
repository metadata:

| Component | Version | Reference |
| --- | --- | --- |
| Android Gradle Plugin | 9.4.1 | [Google Maven metadata](https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/maven-metadata.xml), [AGP 9.4 compatibility](https://developer.android.com/build/releases/agp-9-4-0-release-notes) |
| Gradle | 9.8.0 | [Gradle release metadata](https://services.gradle.org/versions/current) |
| Kotlin | 2.4.20 | [Kotlin release history](https://kotlinlang.org/docs/releases.html) |
| Dokka | 2.2.0 | [Maven Central metadata](https://repo.maven.apache.org/maven2/org/jetbrains/dokka/dokka-gradle-plugin/maven-metadata.xml) |
| Maven Publish plugin | 0.37.0 | [Vanniktech changelog](https://vanniktech.github.io/gradle-maven-publish-plugin/changelog/) |
| JUnit | 4.13.2 | [JUnit 4 releases](https://github.com/junit-team/junit4/releases) |
| Android test runner | 1.7.0 | [AndroidX Test release notes](https://developer.android.com/jetpack/androidx/releases/test) |

Use JDK 21, Python 3.11 or newer for artifact inspection, Android platform
`platforms;android-37.0`, and AGP's default `build-tools;36.0.0`. Gradle's wrapper
pins both the distribution version and SHA-256 checksum; its JAR and scripts
were regenerated with Gradle 9.8.0. The expected wrapper JAR SHA-256 is
`238e777fcddd7e34f9708186085def2abd6e08e658505b38718d79d74c21abd5`.

Kotlin is enabled by AGP itself. The catalog's Kotlin Gradle plugin dependency
only upgrades the built-in compiler as described in [AGP's migration notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes).
Do not apply a second Kotlin Android plugin. Both modules emit Java 8 bytecode.

Both release variants retain the repository's API 15 minimum. Debug variants use
API 21 because the maintained Android test runner needs it. The release AAR sets
`minCompileSdk=28` explicitly instead of inheriting the library's compile SDK 37;
that does not guarantee compatibility with old D8/R8 or Kotlin compilers. Use
current Kotlin/Android tooling to consume the new snapshot, and consult
[Android's Kotlin compiler compatibility requirements](https://developer.android.com/build/kotlin-support).
The historical Maven Central release 1.11 has an API 10 minimum.

## Verification

With `ANDROID_HOME` pointing at your SDK, run:

```sh
./gradlew :dialogplus:testDebugUnitTest \
  :dialogplus:lint :dialogplus:lintRelease :app:lint :app:lintRelease \
  :dialogplus:assembleRelease :dialogplus:assembleDebugAndroidTest \
  :app:assembleDebug :app:assembleRelease :dialogplus:publishToMavenLocal \
  -PlocalPublication -Dmaven.repo.local="$PWD/build/maven-local"
python3 scripts/check-public-api.py
python3 scripts/check-publication.py
python3 scripts/check-readme.py
git diff --check
```

For real Android view, adapter, animation and back-button tests, attach an API 21+
device or emulator and run `./gradlew :dialogplus:connectedDebugAndroidTest`.
`ANDROID_SERIAL` can select a specific device. The test host is in the debug
source set and is excluded from the release AAR and release sources JAR.

The JVM suite exercises margin defaults, drag boundaries, snap thresholds, and
height interpolation without Robolectric or default-returning Android stubs.
Android execution covers holder inflation, existing-view reparenting, fixed and
scrolling headers, item/null-item callbacks, content dimensions, dialog ownership,
show/dismiss idempotence, asynchronous dismissal, back cancellation, overlay
cancellation, and list scroll position. JUnit assertions and recording callbacks
replace assertion and mocking libraries. There are no platform API fakes.

Local validation passed 7 JVM tests and 14 device tests on an Android 15/API 35
arm64 emulator, both release lint tasks, library release assembly, sample debug
and release assembly, Dokka generation, local publication, and all three inspection
scripts. The workflow also passed actionlint 1.7.12 validation. The local run did not execute on API 15–34 or API 36–37, physical devices,
or gesture navigation. CI additionally schedules API 21 and API 35 emulator tests;
its remote execution must be confirmed after these changes are pushed.

Release lint reports no errors. Its remaining warnings are two platform
`TargetApi` annotation recommendations in the library and eight sample warnings
(seven layout/style warnings and one platform annotation recommendation). Gradle reports a deprecated configuration visibility call
from external build plugins. No lint errors are disabled or baselined.

## Dependency boundaries

The release POM and library variants in Gradle metadata declare only
`org.jetbrains.kotlin:kotlin-stdlib:2.4.20`. Its resolved runtime graph also
contains `org.jetbrains:annotations:13.0`. Keep this upstream dependency; removing
it with exclusions is not necessary to simplify this library.

`junit:junit:4.13.2` and its Hamcrest runner dependency are JVM test-only.
`androidx.test:runner:1.7.0` and its JUnit, monitor, tracing, and annotation
transitives are device-test-only. AGP, Kotlin compiler, Dokka, and Maven Publish
are build plugins/dependencies, not consumer runtime dependencies. The sample
uses platform widgets and has only the project dependency; AppCompat, Material,
Flexbox, and Kotlin synthetic view accessors are no longer needed.

Inspect the actual graphs after changes:

```sh
./gradlew :dialogplus:dependencies --configuration releaseRuntimeClasspath
./gradlew :dialogplus:dependencies --configuration debugUnitTestRuntimeClasspath
./gradlew :dialogplus:dependencies --configuration debugAndroidTestRuntimeClasspath
```

## API and behavior compatibility

`api/java-public-api.txt` is an intentional compatibility fixture captured from
the pre-migration Java sources with `javap -protected -s`. The check script compares
124 original JVM constructors/methods across 14 public types, static and abstract
modifiers, and subclass/member overridability. It checks the actual release AAR.
This is a signature check, not proof of complete binary or behavioral compatibility.
The factory keeps `@JvmStatic` and a non-final JVM bridge; the existing builder
methods, explicit overloads, holder constructors, listener interfaces, and
`SimpleAnimationListener` remain available to Java consumers.

Kotlin callers use properties for getters and fluent functions for builder
setters. Code implementing `Holder` in Kotlin must implement `inflatedView`,
`header`, and `footer` as properties. Calling Java-style `getHeader()` directly
from Kotlin or assigning synthetic builder properties should be changed to these
properties/fluent functions. Previously package-private builder/dialog
constructors are Kotlin `internal`, which makes their JVM constructors public;
consumers should continue to use `DialogPlus.newDialog`.

Nullable listeners may be cleared with null. Unset header/footer/adapter/listener
getters are explicitly nullable. Adapter click items and clicked views are
nullable because Android can send null for scrolling headers/footers or synthetic
item clicks. Required contexts, holders, adapters, and added views reject null at
the Kotlin entry point; Java code that previously passed null may fail earlier.
Accessing a holder's content before inflation can now throw
`IllegalStateException`/`UninitializedPropertyAccessException` rather than returning
null despite its old `@NonNull` contract. Missing key listeners likewise fail with
`IllegalStateException`. The incorrect old `@ColorRes` contract was replaced with
a description accepting drawable/color resources, matching `setBackgroundResource`.

Focused fixes accompany the conversion:

- A fixed ListHolder header no longer subtracts one from item positions; only
  scrolling ListView headers contribute to the offset.
- `isShowing` checks each instance's attachment, so another instance cannot
  prevent it from showing. Dismissing an unattached/already-dismissed dialog is a
  no-op and does not emit a spurious dismissal callback.
- On API 33+, the dialog owns and unregisters a platform back callback; legacy
  key handling remains for older Android. New API types are isolated in a helper
  only loaded behind the API check. Gesture animation progress is not implemented.
- Expansion measures the available Activity content/window instead of deprecated
  display height and private system dimension resources; drag cancellation resets
  coordinates, collapse resets fullscreen state, and a scrolled list is not
  mistaken for the top solely because its first visible child aligns with padding.
- The sample uses safe numeric parsing, respects entered widths, handles window
  insets, and dismisses its owned dialog during Activity destruction.

## Publishing

Coordinates and namespace remain `com.orhanobut:dialogplus` and
`com.orhanobut.dialogplus`. Vanniktech publishes the release AAR, its dependency
metadata, Kotlin sources, and Dokka HTML in the Maven `javadoc` JAR through
[Sonatype Central Portal](https://vanniktech.github.io/gradle-maven-publish-plugin/central/).
Automatic release is disabled. Nothing in the ordinary verification workflow
uploads to Sonatype or uses publishing credentials.

Before an authorized remote release, migrate/verify the `com.orhanobut` namespace
in the Central Portal and supply `mavenCentralUsername`, `mavenCentralPassword`,
`signingInMemoryKey`, and `signingInMemoryKeyPassword` through user-level Gradle
properties or secret environment variables. Keep all values and private keys out
of this repository. Set `VERSION_NAME` for the authorized release. Normal
publishing requires signing; `-PlocalPublication` makes signing optional solely
for the unsigned local inspection command above and must never be used for a
remote upload. Local artifacts were inspected without uploading or signing a
remote release; Central Portal credentials, namespace ownership, remote signing,
and release validation remain unverified.

## Remote CI retirement

Local Travis configuration and Checkstyle enforcement have been removed. The new
GitHub Actions workflow exposes `Verify`, `Device tests (API 21)`, and
`Device tests (API 35)` checks with read-only repository permissions and no release
credentials.

During this task, anonymous GitHub API requests to branch protection and hooks
returned HTTP 401. The available browser was signed out and the settings page
was inaccessible. The public current-master commit status endpoint returned no
statuses; that does not establish that Travis is disabled. No remote integration,
hook, or required status check was changed.

A repository administrator should perform these steps for `orhanobut/dialogplus`:

1. After the modernization is pushed, confirm all three new Actions checks pass.
2. Inspect [branch protection](https://github.com/orhanobut/dialogplus/settings/branches)
   and [rulesets](https://github.com/orhanobut/dialogplus/settings/rules). Replace any
   required `continuous-integration/travis-ci/push` or
   `continuous-integration/travis-ci/pr` checks with the equivalent new checks,
   retaining the rest of the protection policy.
3. Inspect [repository webhooks](https://github.com/orhanobut/dialogplus/settings/hooks)
   for Travis callbacks. Disable/remove the Travis hook if present. In repository
   Settings → Integrations → GitHub Apps, configure any Travis installation to
   remove this repository from its selected repositories; do not uninstall it
   from unrelated repositories.
4. In Travis CI's repository settings for `orhanobut/dialogplus`, deactivate the
   repository and disable push, pull-request, and scheduled/cron triggers. Confirm
   later pushes and pull requests run Actions without creating new Travis jobs.

Until authenticated settings inspection confirms those steps, Travis is retired
only from the local repository, not proven disabled remotely.
