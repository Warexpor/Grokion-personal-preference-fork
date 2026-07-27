# Changelog

## 2.1.127-grokion — 2026-07-27

### Fixed
- Cold-start crash: SQLCipher migration no longer probes already-encrypted
  `chat_database` as plaintext (`SQLiteNotADatabaseException`).
- Stream stick-to-bottom no longer thrash-scrolls every token.

### Changed
- Model chip pinned top-right; Ask tab chrome removed.
- Message action rows match Grok placement (user edit/copy on tap; regenerate
  under assistant); equal icon gaps.
- App-wide Inter; GROKION history wordmark uses Iceland.
- Slimmed bundled selectable fonts to Inter (+ Iceland brand).

## 2.1.126-grokion — 2026-07-27

### Changed
- Grok Ask shell parity: tall composer with in-panel model chip, Ask tab chrome,
  history drawer (profile header, title+time+overflow, bottom search/settings/new),
  settings as grouped cards with X close.
- Shared fragment stack motion + smoother history drawer / send morph.
- Semantic dark palette tuned to Grok canvas/surfaces; monochrome switches.

### Fixed
- Mic gated behind extended dock; expandable-input collapse height; model picker
  LAN/OpenRouter navigation container; history→settings drawer race.

### Removed
- Live STT (mic, Voice settings, assist auto-STT, watermark hold-to-talk,
  Transactivity). TTS and transcription-model file upload remain.

## 2.1.125-grokion — 2026-07-27

### Security
- Chat Room DB (`chat_database`) encrypted at rest with SQLCipher; passphrase wrapped
  via Android Keystore. Existing plaintext DBs migrate once via `sqlcipher_export`.

### Changed
- Docs/Help/F-Droid metadata aligned with Grokion branding, SQLCipher storage, and
  LAN TLS / destructive-tools settings.

## 2.1.124-grokion — 2026-07-27

### Fixed
- Chat overwrite no longer duplicates messages (delete-then-insert per session).
- Stop/cancel no longer surfaces as an error bubble; single-flight network jobs.
- Stream auto-scroll locks on drag only; TTS cleaned up in `onDestroyView`.
- LAN key migration no longer drops plaintext if Keystore encrypt fails; LAN save
  dialog surfaces encrypt failure.
- MLX model fetch uses `lanHttpClient` (honors trust-self-signed toggle).
- HTTP LAN endpoints validated as private/loopback/`.local` (NSC permits cleartext
  because Android cannot express RFC1918 CIDRs; app-layer blocks public cleartext IPs).

### Security
- Cleartext allowed for LAN HTTP with host validation; OpenRouter remains HTTPS.
- LAN self-signed TLS is an opt-in Settings toggle.
- LAN API key encrypted via Android Keystore; backup excludes prefs + chat DB.
- Autosend confirmation; biometric gate on share/assist/spell-check when enabled.
- Tools default off; destructive file tools gated; import size limits; settings-action allowlist.

### Changed
- Grok shell MVP: single-row Ask-anything composer, sparse top bar, history slide-over,
  copy icons, scrim dim, warm light canvas + theme-aware Markwon/switches.
- Extracted `SseJsonReader`, `ChatSessionSaver`, `ToolExecutorPolicy`, `WorkspacePaths`,
  `LanEndpointValidator`.
- Dropped unused Navigation Component dependencies.

## 2.1.123-grokion — 2026-07-27

### Changed
- **Identity rebrand (Phase 3):** user-visible strings, themes (`Theme.Grokion`), HTTP
  User-Agent / OpenRouter headers, workspace folder (`Download/grokion` with legacy
  `oxproxion` read fallback), and tool names (`list_grokion_files`, `read_grokion_file`;
  old names still accepted at runtime).

## 2.1.122-grokion — 2026-07-18

### Changed
- **Auto-save toast removed:** the "Chat saved: ..." toast no longer pops up on
  every message — auto-save runs silently in the background.
- **Title generated once, not per-save:** auto-save now reuses the existing
  session title on subsequent saves. The AI title generation only fires on the
  very first save of a new chat.

## 2.1.121-grokion — 2026-07-17

### Fixed
- **Auto-save only fired once:** removed the `autoSaved` one-shot gate so the
  chat is re-saved with latest messages on every streaming completion, not
  just the first.
- **Title generation swallowed failures:** `autoSaveChat()` now catches
  exceptions from `getSuggestedChatTitle()` without losing the fallback title.
  API error messages (e.g. "Error: 401 ...") are detected and discarded so
  the fallback (first user message or timestamp) is used instead.

## 2.1.120-grokion — 2026-07-16

### Added
- **Auto Save Chats** — new toggle in Settings. When enabled, the chat is
  automatically saved with an LLM-generated title after the first assistant
  response completes streaming.
- `autoSaveChat()` function in ChatViewModel using the existing
  `getSuggestedChatTitle()` infrastructure (was previously unused).
- `SharedPreferencesHelper` getter/setter for auto-save preference.

## 2.1.119-grokion — 2026-07-16

### Fixed
- **Light theme crash (root cause):** `MainActivity.onCreate()` was hardcoding
  `AppCompatDelegate.MODE_NIGHT_YES` on every launch, overriding the user's
  saved theme preference. Changed to read from `SharedPreferencesHelper` and
  apply the saved mode before `super.onCreate()`. This was causing an
  infinite recreation loop when Light mode was selected (MainActivity fought
  ChatFragment's theme listener, each forcing opposite modes).

### Changed
- **Model chip maxWidth** increased from 140dp to 180dp for longer model names.
- **Menu panel** fades in/out (200ms) instead of instant toggle.
- **Dim overlay** fades with the menu panel.
- **Send button** icon cross-fades between send ↔ stop (100ms).
- **Chat frame** has `animateLayoutChanges="true"` for smooth transitions.
- **RecyclerView** item appearance animation via `SimpleItemAnimator`.

### Fixed
- Removed stale `bgreen.xml` and `Widget.Grokion.IconButton.Pill` style.

## 2.1.116-grokion — 2026-07-16

### Fixed
- **Light theme crash (proper fix):** created `values-night/colors.xml` with
  all dark xAI colors and rewrote `values/colors.xml` with light-mode
  equivalents. Theme now uses `Theme.Material3.Light.NoActionBar` in
  `values/` and `Theme.Material3.Dark.NoActionBar` in `values-night/`.
  Android's resource qualifier system handles the swap automatically when
  the user picks a different theme mode.
- **Theme toggle restored:** Settings now has System / Dark options (no
  Light-only option — just System and Dark, both properly supported).
- **ChatFragment theme code** restored to respect saved theme preference
  instead of hardcoding `MODE_NIGHT_YES`.

## 2.1.115-grokion — 2026-07-16

### Fixed
- **Model chip overlap:** constrained `modelNameTextView` between
  `openSavedChatsButton` and `saveChatButton` (start_toEndOf / end_toStartOf),
  reduced `maxWidth` from 220dp to 160dp — no more clipping into neighbors.
- **Light theme crash:** removed the Light / System theme toggle from Settings.
  Grokion now always forces `MODE_NIGHT_YES` (dark-only). Light mode would make
  all xAI colors invisible — completely unusable.
- **Theme toggle remnants:** removed orphaned `MaterialButtonToggleGroup` + theme
  handling code from SettingsFragment and ChatFragment.
- **Theme default:** `getThemeMode()` now defaults to `THEME_DARK` instead of
  `THEME_SYSTEM` — no risk of light mode on first launch.

## 2.1.114-grokion — 2026-07-16

### Fixed
- **Burger position:** composer `gravity` changed from `bottom` to `top` so
  buttons align at the top of the input field, not pushed to the bottom.
- **Missing "new chat" button:** `resetChatButton` visibility restored to
  `visible` by default (was `gone`), matching original oxproxion layout.
- **10 hardcoded `#FF7A17` orange values** across 8 Kotlin files replaced
  with `#FF7D8187` grey — these bypassed colors.xml entirely and kept
  toggle switches, selected states, and text highlights orange.
- `xai_selected` color changed from reddish-brown `#FF2A1A0A` to
  dark grey `#FF2A2A2A` to match the grey accent palette.

## 2.1.113-grokion — 2026-07-16

### Fixed
- Removed 18 commented-out `Log.*` debug statements from ChatFragment.kt
  (abandoned debug code cluttering the source).
- Deleted stale `ic_launcherrobby.png` icons from all mipmap densities
  (typo leftover, not referenced by any resource).

## 2.1.112-grokion — 2026-07-16

### Changed
- **Assistant message button bar:** reduced visual noise from 10 to 5 visible
  buttons. Export actions (PDF, Markdown, PNG, HTML, Save) hidden by default;
  core actions remain: copy, edit, share, speak (TTS), collapse toggle.
- **Model chip:** added `xai_canvas_soft` fill for a more prominent Grok-style
  pill (was transparent).
- **Menu panel:** increased padding (16dp → 20dp), row spacing (10dp/12dp →
  14dp/16dp), and button gaps (10dp → 12dp) for a premium, airier layout.

## 2.1.111-grokion — 2026-07-16

### Changed
- Accent colors: `xai_accent_sunset`, `xai_accent_sunset_soft`, and
  `xai_progress` changed from orange to grey (`#7D8187` / `#DADBDF`).
  Affects toggle switches, cursor, text selection highlight, progress bar,
  dialog accents, selected states, and outlined message borders.
- Mic visibility: reverted `speechButton` logic to match original oxproxion
  — hidden when `isExtendedDockEnabled` is off (default).

## 2.1.110-grokion — 2026-07-16

### Changed
- Bottom composer: full Grok-clean restyle — all non-send buttons use
  transparent backgrounds so icons float inside the pill; only send button
  keeps filled grey circle (`xai_canvas_mid`) as primary CTA.
- Container padding increased from 2dp to 4dp for better breathing room
  inside the pill's rounded corners.
- All pill buttons now have `app:rippleColor="@color/xai_canvas_mid"` for
  subtle touch feedback on transparent backgrounds.
- Layout structure reverted to match original oxproxion (horizontal container,
  vertical button stacks, 48dp IconButton.Filled) to restore programmatic
  expanded/collapsed state logic — the Grok look comes from styling, not
  structural changes.

## 2.1.109-grokion — 2026-07-16

### Changed
- Composer pill: added `elevation="2dp"` for subtle shadow separation from canvas.
- Send button: fixed asymmetric margins (`4dp/1dp` → `2dp/2dp`) for balanced spacing.
- Composer container: tightened bottom padding (`10dp` → `8dp`) to match Grok's compact feel.
- EditText: reduced `minHeight` from `28dp` → `24dp` for tighter single-line input.
- Icon vectors (`ic_grok_menu`, `ic_send`, `ic_mic`): normalized to 8-digit ARGB hex for consistent theming.

### Added
- Custom pill icon button style (`Widget.Grokion.IconButton.Pill`) with zero padding/overrides applied to all 7 pill buttons — eliminates Material3 default icon padding that caused misalignment.

## 2.1.108-grokion — 2026-07-16

### Changed
- Composer pill: tightened proportions (48dp minHeight, tighter padding, smaller icon targets).
- Empty state watermark: replaced raster PNG with exact Grok vector logo (ic_vector_grok_logo paths).
- Send button: defined `bgreen` color (#FF1CAB55) matching Grok iOS green.
- Stop icon: replaced with exact Grok `ic_vector_stop` (rounded square).

### Fixed
- Build error from missing `bgreen` color resource.

## 2.1.107-grokion — 2026-07-16

### Changed
- App display name and id rebranded to **Grokion** (`io.github.warexpor.grokion`).

## 2.1.106-grokilike — 2026-07-16

### Changed
- `applicationId` set to `io.github.warexpor.grokilike` (no longer shares identity with upstream oxproxion / F-Droid updates).
- FileProvider authority uses `${applicationId}.fileprovider`.

## 2.1.105-grok — 2026-07-16

### Changed
- App display name set to Grokilike (superseded by Grokion).
- Launcher icon uses official Grok mark from Grok.ipa (adaptive + density mipmaps, black canvas).

### Added
- Hardened SSE streaming: proper multi-line `data:` frames, keepalive comments, NDJSON fallback for LAN servers, `Accept: text/event-stream` on stream requests.
- Streaming enabled by default for new installs.

### Fixed
- Stream chunk models tolerate missing optional fields (id/model/delta) from sparse providers.
- Restored attach / image-upload top-bar buttons (were `gone` after shell rewrite).
- System-message control visible in composer again.
- Mic always available; clear only when extended dock + text (was hiding mic when dock off).
- Empty-state (mark + prompt) correctly fades under menu and restores on dismiss.
- Extended top bar: parent `HorizontalScrollView` visibility tracks preference (row was permanently hidden).
- Secondary screens/dialogs token drift: licenses, markdown viewer, edit message, LAN dialog, preset chooser.

### Changed
- Chat shell rebuilt for Grok iOS feel: minimal top bar (history · model chip · save), quiet empty state, bottom pill composer.
- Empty state uses Grok mark + "What do you want to know?" (inspired by Grok.ipa 1.3.94).
- Composer: single pill field, "Ask anything" hint, mic + white send CTA; secondary controls hidden until needed.
- Assistant messages: flat full-width text (no bubble); user messages: soft rounded chips, right-aligned.
- Thinking state: soft alpha pulse instead of heavy bubble color flash.
- App display name set to "Grok".

### Added (UI)
- Grok shell drawables: `bg_composer_pill_tall`, `bg_top_bar_grok`, `bg_model_chip`, `bg_menu_panel`, `ic_grok_mark`.

## 2.1.103-xai — 2026-07-16

### Changed
- Full UI overhaul to xAI visual language: near-black canvas (`#0a0a0a`), white/ink text, hairline borders, sunset orange accent (`#ff7a17`).
- Forced dark theme only (xAI is dark-canvas only); light Material theme removed from day/night resources.
- Message bubbles, dialogs, inputs, icon chrome, and button selectors remapped to xAI tokens.
- Runtime hardcoded palette hex values updated to match the new system.

### Added
- Named color tokens (`xai_*`) in `values/colors.xml` for canvas, hairline, body, mute, sunset accent, links, and errors.
