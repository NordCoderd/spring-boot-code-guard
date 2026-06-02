# Sonar Fixes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Clear all SonarCloud issues + pass Quality Gate. Fix hardcoded versions, missing dep verification, mutable collections, duplicate literals.

**Architecture:** Move dep versions to `gradle/libs.versions.toml` catalog. Enable Gradle dependency verification (writes `gradle/verification-metadata.xml`) — kills both `text:S8569` vuln + LOW hotspot. Convert `mutableSetOf` builders to immutable `toSet()`. Extract repeated `"no package"` literal to constant in `PackageRules.kt`.

**Tech Stack:** Gradle 9.4 Kotlin DSL, Gradle version catalogs, Gradle dependency verification, Kotlin 2.3.

**Sonar issues fixed:**
- `text:S8569` (vuln) — dep verification missing → enable verification-metadata
- `text:S8569` build.gradle.kts:24 — kotlin-compiler-embeddable hardcoded → catalog
- `kotlin:S6624` build.gradle.kts:27,28,29 — Spring/Hibernate hardcoded → catalog
- `kotlin:S6524` ProxyRules.kt:139,140,146 — mutable collections → `toSet()`
- `kotlin:S1192` PackageRules.kt:144 (+11 dup sites) — `"no package"` literal → constant
- LOW hotspot — dep verification missing → resolved by verification-metadata
- QG `new_security_rating=3` → resolved when vuln cleared
- QG `new_security_hotspots_reviewed=0%` → mark hotspot Reviewed (manual or via API)

---

## File Structure

- Create: `gradle/libs.versions.toml` — version catalog
- Create: `gradle/verification-metadata.xml` — generated dep verification (committed)
- Modify: `build.gradle.kts` — switch deps to catalog refs
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/proxy/ProxyRules.kt` — immutable sets
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt` — `NO_PACKAGE` const

---

### Task 1: Version Catalog

**Files:**
- Create: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts:21-30`

- [ ] **Step 1: Create catalog**

`gradle/libs.versions.toml`:
```toml
[versions]
konsist = "0.17.3"
kotlin-compiler = "2.0.21"
spring-boot = "4.0.5"
hibernate-validator = "9.1.0.Final"

[libraries]
konsist = { module = "com.lemonappdev:konsist", version.ref = "konsist" }
kotlin-compiler-embeddable = { module = "org.jetbrains.kotlin:kotlin-compiler-embeddable", version.ref = "kotlin-compiler" }
spring-boot-starter-data-jpa = { module = "org.springframework.boot:spring-boot-starter-data-jpa", version.ref = "spring-boot" }
spring-boot-starter-web = { module = "org.springframework.boot:spring-boot-starter-web", version.ref = "spring-boot" }
hibernate-validator = { module = "org.hibernate.validator:hibernate-validator", version.ref = "hibernate-validator" }
```

- [ ] **Step 2: Switch dependencies block**

`build.gradle.kts` replace lines 21-30:
```kotlin
dependencies {
    api(libs.konsist)
    implementation(libs.kotlin.compiler.embeddable)

    testImplementation(kotlin("test"))
    testImplementation(libs.spring.boot.starter.data.jpa)
    testImplementation(libs.spring.boot.starter.web)
    testImplementation(libs.hibernate.validator)
}
```

- [ ] **Step 3: Verify build resolves**

Run: `./gradlew dependencies --configuration testRuntimeClasspath -q | head -20`
Expected: dep tree prints, no `Could not resolve` errors.

- [ ] **Step 4: Run tests**

Run: `./gradlew test`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
git add gradle/libs.versions.toml build.gradle.kts
git commit -m "build: extract dep versions to version catalog"
```

---

### Task 2: Dependency Verification

**Files:**
- Create: `gradle/verification-metadata.xml`

- [ ] **Step 1: Generate metadata**

Run:
```bash
./gradlew --write-verification-metadata sha256 help
```
Expected: writes `gradle/verification-metadata.xml`. May take 1-3 min downloading checksums.

- [ ] **Step 2: Inspect file head**

Run: `head -20 gradle/verification-metadata.xml`
Expected: `<verification-metadata>` root, `<components>` block with sha256 entries.

- [ ] **Step 3: Validate verification mode**

Edit `gradle/verification-metadata.xml`, ensure `<verify-metadata>true</verify-metadata>` and `<verify-signatures>false</verify-signatures>` present in `<configuration>` block (pgp signing not in scope).

- [ ] **Step 4: Re-run build with verification**

Run: `./gradlew clean test`
Expected: BUILD SUCCESSFUL. Verification passes silently.

- [ ] **Step 5: Commit**

```bash
git add gradle/verification-metadata.xml
git commit -m "build: enable gradle dependency verification (sha256)"
```

---

### Task 3: Immutable Sets in ProxyRules

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/proxy/ProxyRules.kt:139-146`

- [ ] **Step 1: Run existing tests for proxy rules**

Run: `./gradlew test --tests "*Proxy*" -q`
Expected: PASS. Establishes green baseline.

- [ ] **Step 2: Replace mutable builders**

Edit lines 139-146. Old:
```kotlin
val ownProxyNames = ownMethods.mapTo(mutableSetOf()) { it.n }
val ownNames = functions.mapTo(mutableSetOf()) { it.name }
```
New:
```kotlin
val ownProxyNames = ownMethods.mapTo(mutableSetOf()) { it.n }.toSet()
val ownNames = functions.mapTo(mutableSetOf()) { it.name }.toSet()
```

Old line 146:
```kotlin
val ambiguous = methods.keys.filterTo(mutableSetOf()) { name ->
```
New:
```kotlin
val ambiguous: Set<String> = methods.keys.filterTo(mutableSetOf()) { name ->
```

Then close the lambda and append `.toSet()`. Result:
```kotlin
val ambiguous: Set<String> = methods.keys.filter { name ->
    functions.any { it.name == name && it.proxyMethod() == null }
}.toSet()
```

(Replaces the `filterTo(mutableSetOf())` builder pattern with `filter { }.toSet()`.)

- [ ] **Step 3: Run tests**

Run: `./gradlew test --tests "*Proxy*" -q`
Expected: PASS.

- [ ] **Step 4: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/proxy/ProxyRules.kt
git commit -m "refactor(proxy): use immutable sets in proxyModels"
```

---

### Task 4: Extract `NO_PACKAGE` Constant

**Files:**
- Modify: `src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt`

- [ ] **Step 1: Locate insertion point**

Run: `grep -n "^class\|^object\|^private const\|^const" src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt | head`
Expected: identifies top-level container. Add const at file top after imports OR inside enclosing object/class as `private const val NO_PACKAGE = "no package"`.

- [ ] **Step 2: Add constant**

Add (companion or top-level, matching codebase style):
```kotlin
private const val NO_PACKAGE = "no package"
```

- [ ] **Step 3: Replace all 12 occurrences**

In `PackageRules.kt` replace literal `"no package"` with `NO_PACKAGE` at lines 144, 172, 201, 231, 301, 327, 360, 395, 433, 469, 536, 563.

Run after: `grep -c '"no package"' src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt`
Expected: `0`.

- [ ] **Step 4: Run package tests**

Run: `./gradlew test --tests "*Package*" -q`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/kotlin/dev/protsenko/codeguard/rules/packages/PackageRules.kt
git commit -m "refactor(packages): extract NO_PACKAGE constant"
```

---

### Task 5: Full Verification + Sonar Re-Scan

**Files:** none

- [ ] **Step 1: Run full pipeline**

Run: `./gradlew clean test detektMain koverVerify koverXmlReport`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 2: Trigger Sonar scan**

Run: `./gradlew sonar`
Expected: scan uploads. May still fail QG until hotspot reviewed (Task 6).

- [ ] **Step 3: Inspect issues**

Run: `./gradlew sonarReport`
Expected: `=== Issues (open) ===` empty (no `[SEVERITY]` lines printed).

If issues remain — read them, fix, re-run. Do not proceed to Task 6 until issue list empty.

---

### Task 6: Mark Hotspot Reviewed

**Files:** none (SonarCloud-side action)

The single LOW hotspot (`Dependencies are not verified...`) is auto-resolved when `verification-metadata.xml` is committed (Task 2) — but SonarCloud may keep it `TO_REVIEW` until the next scan re-classifies. If still open after Task 5:

- [ ] **Step 1: List open hotspots**

Run:
```bash
curl -s -u "$SONAR_TOKEN:" \
  "https://sonarcloud.io/api/hotspots/search?projectKey=NordCoderd_spring-boot-code-guard&status=TO_REVIEW&ps=100" \
  -o /tmp/hotspots.json
python3 -c "import json; d=json.load(open('/tmp/hotspots.json')); [print(h['key'],h.get('message')) for h in d['hotspots']]"
```
Expected: prints hotspot keys still TO_REVIEW. If empty → done.

- [ ] **Step 2: Mark as SAFE**

For each `<key>` printed:
```bash
curl -s -u "$SONAR_TOKEN:" -X POST \
  "https://sonarcloud.io/api/hotspots/change_status" \
  -d "hotspot=<key>&status=REVIEWED&resolution=SAFE&comment=verification-metadata.xml committed"
```
Expected: HTTP 204 (empty response body).

- [ ] **Step 3: Re-fetch QG**

Run: `./gradlew sonarReport`
Expected: `"status":"OK"` in `=== Quality Gate ===` block.

- [ ] **Step 4: Final commit (if any local changes)**

If anything changed locally during scan-iteration:
```bash
git status
git commit -am "chore: sonar quality gate green"
```

---

## Notes

- Dependency verification slows clean builds first time (downloads checksums for every transitive). Subsequent builds: zero overhead.
- Adding new deps later requires `./gradlew --write-verification-metadata sha256 help` again to refresh checksums.
- If a transitive lacks a checksum and verification fails, run the regenerate command — do NOT bypass with `--refresh-keys` or remove verification.
- Version catalog accessor names: dashes in `[libraries]` keys become dots in Kotlin (e.g., `spring-boot-starter-web` → `libs.spring.boot.starter.web`).
