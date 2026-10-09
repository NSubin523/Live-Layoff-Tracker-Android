# Android checks

Use Android Studio's bundled JDK (17 or newer). Core coverage lives in:

- `feature/auth/src/test`: phone formatting, real verification-ID/OTP handoff, wrong-code retry, auto-verification, cancellation, duplicate requests, and input validation.
- `feature/auth/src/androidTest`: Google/phone provider buttons, formatted-number entry, six-digit OTP validation, errors, and disabled submission during verification.
- `feature/feed/src/test`: DTO/cache/domain mapping, optional values, invalid dates, repository success/failure/cancellation, ViewModel loading, refresh, cache retention, network events, and foreground refresh.
- `feature/feed/src/androidTest`: loading, empty/error/content transitions, status and impact display, and pull-to-refresh callback.
- `app/src/test`: startup lifecycle registration and Firebase Messaging subscription telemetry.
- `app/src/androidTest`: guest access, tab navigation, sign-in-sheet routing, state restoration, and an opt-in live Firebase phone check.

```sh
./gradlew :feature:auth:testDebugUnitTest :feature:feed:testDebugUnitTest :app:testDebugUnitTest
ANDROID_SERIAL=emulator-5554 ./gradlew :feature:auth:connectedDebugAndroidTest :feature:feed:connectedDebugAndroidTest :app:connectedDebugAndroidTest
```

For live Firebase phone authentication, configure a fictional number/code in the Firebase console and pass them explicitly. The integration test skips when these arguments are absent. It uses Firebase’s documented fictional-number test mode to skip Play Integrity/reCAPTCHA in the automated check, then restores that setting and signs out the emulator. Production app verification remains enabled; use a dedicated test emulator.

```sh
ANDROID_SERIAL=emulator-5554 ./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.example.tracklayoff.FirebasePhoneSignInTest \
  -Pandroid.testInstrumentationRunnerArguments.testPhone='<configured fictional number>' \
  -Pandroid.testInstrumentationRunnerArguments.testCode='<configured code>'
```

Phone verification uses Firebase's `onCodeSent` verification ID plus the entered code, or signs in directly with the credential from `onVerificationCompleted`. No phone number, OTP, fabricated credential, or app-verification bypass is hardcoded into production code. Google sign-in retains its existing implementation. See [Firebase's Android phone-authentication contract](https://firebase.google.com/docs/auth/android/phone-auth).

A fictional number still needs a valid country/area code. `+15555550100` is invalid and has no recognized region. A suitable US fixture is `+16505550100`, with its code configured in Firebase. The app validates with Google’s libphonenumber metadata before requesting a code.

## Firebase configuration prerequisite

If Firebase returns `SMS unable to be sent until this region enabled by the app developer`, confirm Phone sign-in is enabled, the fictional number/code is saved there, and Authentication → Settings → SMS region policy allows the number’s country. The app cannot override that server-side policy. See [SMS region settings](https://docs.cloud.google.com/identity-platform/docs/admin/sms-regions).

# Generated files

KSP generates Hilt factories and Room implementations during builds. Kotlin, KSP, Gradle, and Android build outputs are local artifacts, excluded by `.gitignore`. They were previously tracked; the cleanup commit removes them from Git's index while preserving local files. Deleting caches is optional troubleshooting and does not prevent regeneration. Builds should never require committing generated files or repeatedly restoring them.

Only source code, build configuration, tests, and intentional schema files belong in reviews. For the one-time generated-file cleanup commit, inspect the deletion-only summary separately from the source-change commit.
