# Android CI and branch policy

Use feature branches and PRs into develop, then promote develop to main via PR.
Neither branch deploys or publishes the app. CI checks debug builds, JVM unit
tests, lint, API 35 emulator tests, secrets in Git history, CodeQL security
analysis, and resolved runtime dependency vulnerabilities (JARs and AARs).

The CI gate requires every job to pass. Fixable HIGH/CRITICAL vulnerabilities
and CodeQL security severity >=7 block merging. Full reports retain unfixed
findings; reports expire after seven days. Missing reports fail closed.

CI uses fake Firebase configuration, no real credentials or signing keys.
The opt-in Firebase phone test skips without fictional test-phone arguments;
normal UI, database, and worker tests still run. CI APKs are validation builds.

Main requires PRs, zero approvals, CI gate from GitHub Actions, an up-to-date
branch, and resolved conversations. No bypass actors, force pushes or deletion.
Dependabot proposes weekly Gradle and action updates into develop.

Android Studio's Build Variants selector controls the endpoints in core:network.
Debug uses the hosted dev services on physical devices and emulators:

- Chat: https://layoff-chat-dev-241672723896.us-east4.run.app/api/v1/
- Feed, favorites, telemetry: https://layoff-api-dev-241672723896.us-east4.run.app/api/v1/

Release endpoints remain empty until production is configured. No environment
popup, debug badge, or icon change is added. MAC_IP/local-device detection no
longer controls networking. Keep core:network on the matching build variant.

CI passes -PciBuild=true so test APKs use a closed local port instead of live
dev services. This property is for automated tests; normal debug builds use
the URLs above. Keep your real google-services.json for local Firebase testing;
the generated fake CI config must never be used for app distribution.
