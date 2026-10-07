# Chat AI

Text-only chat backed by the shared Python API base URL. The app shell adds a third bottom navigation tab and reuses the existing sign-in sheet for guests.

## Boundaries

- `ui/composables`: render immutable UI state and emit `ChatAction`; scrolling and keyboard behavior stay here.
- `ui/viewmodel`: owns lifecycle-bound jobs, dispatches actions to use cases, and exposes a single `StateFlow`. It has no repository, Firebase, JSON, or HTTP dependency.
- `ui/state/ChatStateReducer`: pure transitions for history, pagination, draft, and reply state.
- `domain/usecase`: history policy and text-reply protocol handling, independently testable.
- `domain/repository/ChatRepository`: history and SSE contracts expressed only through domain models.
- `data`: DTOs, transport, parsing, mapping, and repository implementation.
- `di`: binds implementations and configures a streaming client derived from the shared network client.

## Session and caching

Guests do not request the ChatViewModel. The authenticated route requests an activity-owned Hilt ViewModel on its first visit. Messages and pagination are held in memory across tab changes and rotation; no chat content is written to Room or saved instance state.

Sign-out/account changes cancel jobs and clear messages, draft, and cursor immediately. The next account loads history only when it opens Chat AI. Session version checks reject late responses, and the route verifies state ownership before displaying rows. Process death starts a fresh history load.

## Backend contract

- `GET chat/history?limit=20&before=...`: latest page is ordered oldest-first; older pages use the opaque `next_before` cursor.
- `POST chat`, JSON `{ "message": "..." }`, accepting `text/event-stream`.
- SSE events: `intent`, `cards`, `text`, `error`, `done`. Cards are decoded but not rendered in this MVP.
- User identity comes from the shared bearer-token interceptor, never the request body.
- Prompts allow up to 500 Unicode code points.

One assistant row grows by appending exact text deltas. Errors preserve partial text. EOF without `done` is interrupted. POSTs are never automatically retried. The streaming client removes body logging to prevent buffering, uses a 90-second idle read timeout, and closes on cancellation.

The backend does not stream persisted message IDs. Locally created rows keep local IDs during the session; a fresh session loads canonical server IDs. No text-based reconciliation or automatic history refresh replaces live rows.

## Verification

Run with JDK 17 or newer:

```sh
./gradlew :feature:chat-ai:testDebugUnitTest :feature:chat-ai:lintDebug :app:assembleDebug
```

With an Android emulator/device available:

```sh
./gradlew :feature:chat-ai:connectedDebugAndroidTest
```

Unit tests cover mapping, real HTTP SSE transport, protocol completion/errors, reducer behavior, duplicate sends, and session cleanup. Compose tests cover guest access, text input, loading/errors, and partial replies.

Future voice, transcription, playback, or feedback features should introduce their own focused contracts/use cases rather than expand this repository into a general media service.
