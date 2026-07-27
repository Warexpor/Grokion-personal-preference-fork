# Grokion ↔ Grok Android UI — 1:1 design contract

**Status:** implementation source of truth for chat-shell visual parity  
**Target product:** `ai.x.grok` **1.1.97-release.01** (local extract under `references/`)  
**Secondary refs:** iOS conversation chrome teardown; Play screenshots for Ask / history / voice  
**Scope:** Ask (chat) surface UI elements — not SuperGrok paywalls, Imagine feed, or LiveKit voice pipeline logic  
**Out of product scope (document only):** Imagine tab, App Builder, connectors marketplace, X citation live graph

This document is the gap list and the pixel/token contract. Implement against **§9 Gap matrix** until every row is `Done`.

---

## 1. Sources (priority order)

| Priority | Artifact | What it unlocks |
|----------|----------|-----------------|
| 1 | `references/grok_decompiled` (jadx) | Real tokens: `k9.h` / `k9.i` (`GrokSemanticColors`), `j80.a` (`HorizonThemeColors`), fonts, strings, drawables |
| 2 | `references/grok_base.apk` + `grok_xxhdpi.apk` | Re-extract if decompile is stale |
| 3 | Live Grok app on device | Motion, haptics, empty states |
| 4 | Public iOS DESIGN.md teardowns | Conversation transcript rules (assistant = no bubble) when APK obfuscation hides Compose sizes |

Do **not** treat x.ai marketing-site tokens (Universal Sans, sunset `#FF7A17`, outline pills) as chat-shell truth. Marketing ≠ app.

---

## 2. Information architecture (Ask surface)

```
┌─────────────────────────────────────────────┐
│ Top bar: menu | model/mode chip | new/more  │
├─────────────────────────────────────────────┤
│                                             │
│  Transcript (empty → glyph + suggestions)   │
│  · user: right bubble                       │
│  · assistant: full-width plain text         │
│  · sources strip + citation cards (opt)     │
│  · action row after stream                  │
│                                             │
├─────────────────────────────────────────────┤
│ Composer pill (pinned): + | Ask anything |↗ │
└─────────────────────────────────────────────┘
         ▲ history slide-over from left
```

**Official Android also has bottom tabs** `Ask` | `Imagine` (`grok_home_chat_tab_title` / `grok_home_image_tab_title`). Grokion ships **Ask-only**; do not add Imagine chrome unless product asks. Voice is an entry from composer / settings, not a third tab in Grokion.

**History** = left slide-over (~84% width): Search, Pinned, Conversations list, settings entry.  
**Settings** = pushed stack (Appearance, Haptics, Voice, Data, Usage, …).

---

## 3. Theme modes (from APK)

| Settings label | Internal (`u70.b`) | Canvas character |
|----------------|--------------------|------------------|
| Light | light semantic set | Near-white `#FCFCFC` / warm XML `appBackground` `#F8F7F5` |
| Dark | `STANDARD` / dark semantic | Near-black `#050505` (not Material `#121212`) |
| System | follow OS | maps to Light or Dark |
| For You | `DIM` | X-blue night `#15202B` |
| (internal) | `LIGHTS_OUT` | True void / blacker OLED set |

**Grokion default for parity:** Dark (`#050505`) as primary shipped look. Keep Light. Optional later: For You / Lights Out.

---

## 4. Color system

### 4.1 `GrokSemanticColors` (chat — authoritative)

Decoded from `k9.h` + `k9.i` (`GrokSemanticColors(isLight, backgroundHigh, backgroundLow, foregroundPrimary, foregroundSecondary, foregroundTertiary, foregroundDestructive, surfaceBright, surfaceDim, surfaceNeutral, surfaceOverlay, border)`).

#### Dark (primary)

| Token | Hex | Role |
|-------|-----|------|
| `backgroundHigh` | `#050505` | Screen canvas |
| `backgroundLow` | `#181818` | Recessed / secondary canvas |
| `foregroundPrimary` | `#FCFCFC` | Body, titles, icons active |
| `foregroundSecondary` | `#9E9E9E` | Meta, placeholders, idle icons |
| `foregroundTertiary` | `#636363` | Hints, disabled |
| `foregroundDestructive` | `#FF4245` | Errors (dark) |
| `surfaceBright` | `#242424` | Elevated chips, pressed send disabled, suggestion active |
| `surfaceDim` | `#050505` | Dim surface (= canvas) |
| `surfaceNeutral` | `#181818` | User bubble, composer field |
| `surfaceOverlay` | `#FCFCFC` @ 6% | Scrims / overlays on dark |
| `border` | `#FCFCFC` @ 8% | Hairlines on dark |

#### Light

| Token | Hex | Role |
|-------|-----|------|
| `backgroundHigh` | `#FCFCFC` | Canvas |
| `backgroundLow` | `#F2F2F2` | Soft / composer fill |
| `foregroundPrimary` | `#000000` | Body |
| `foregroundSecondary` | `#636363` | Meta |
| `foregroundTertiary` | `#9E9E9E` | Placeholder |
| `foregroundDestructive` | `#F4212E` | Errors |
| `surfaceBright` | `#FCFCFC` | Bright surface |
| `surfaceDim` | `#DFDFDF` | Disabled send fill |
| `surfaceNeutral` | `#F2F2F2` | User bubble / composer |
| `surfaceOverlay` | `#000000` @ 6% | Overlay |
| `border` | `#000000` @ 6% | Hairlines |

XML also defines `appBackground` `#F8F7F5` (warm light splash/widget). Prefer semantic `#FCFCFC` for chat canvas; warm `#F8F7F5` only where Grok uses it for chrome outside transcript.

#### For You / DIM

| Token | Hex |
|-------|-----|
| backgrounds | `#15202B` |
| foregroundPrimary | `#FFFFFF` |
| foregroundSecondary / Tertiary | `#8899A6` |
| surfaces | `#101922` |
| border | `#38444D` |
| destructive | `#F4212E` |

### 4.2 Greyscale ramp (full `k9.h` steps)

`#FCFCFC` · `#F7F7F7` · `#F2F2F2` · `#DFDFDF` · `#9E9E9E` · `#858585` · `#636363` · `#484848` · `#363636` · `#242424` · `#181818` · `#0F0F0F` · `#050505` · `#000000`

### 4.3 Chromatic accents (Horizon / e0 — use sparingly)

| Token | Hex | Allowed use |
|-------|-----|-------------|
| `link` / X blue | `#1D9BF0` | Inline links, citation taps, verified check |
| `link` pressed | `#1A8CD8` / `#006FD6` | Pressed links |
| `success` | `#00BA7C` | Copy confirmed |
| `error` (light) | `#F4212E` | Banners, destructive |
| `error` (dark) | `#FF4245` | Banners |
| `warning` | `#FFD400` | Rare disclaimer emphasis |
| Accent white | `#FFFFFF` | Enabled send fill, active mode pill |
| Pressed white | `#D7DBDC` | Send pressed |

**Rule:** no decorative brand orange/purple on Ask chrome. White (or black on light) is the control accent. Link blue is the only routine chroma.

### 4.4 Grokion token remap (target `colors.xml`)

| Grokion today | Must become (dark) |
|---------------|--------------------|
| `xai_canvas` `#0A0A0A` | `#050505` (`backgroundHigh`) |
| `xai_canvas_soft` `#16181C` | `#181818` (`surfaceNeutral` / `backgroundLow`) |
| `xai_canvas_card` `#1E2126` | `#242424` (`surfaceBright`) for menus; sheets may stay one step up |
| `xai_canvas_mid` `#272A2E` | `#242424` / `#363636` pressed |
| `xai_hairline` `#2F3336` | `border` = white @ 8% (or `#2F3336` only if visual match after side-by-side) |
| `xai_ink` `#FFFFFF` | `#FCFCFC` |
| `xai_body` `#DADBDF` | `#FCFCFC` primary / `#9E9E9E` secondary as appropriate |
| `xai_mute` `#7D8187` | `#9E9E9E` |
| `xai_accent_sunset` (grey misuse) | **Stop using as chrome accent** → white controls / link blue for links |
| `xai_link` `#A0C3EC` | `#1D9BF0` |
| `xai_error` `#B84A3A` | `#FF4245` dark / `#F4212E` light |
| `xai_progress` grey | `#9E9E9E` or white |

---

## 5. Typography

### 5.1 Families (from APK `res/font`)

| Role | Font file | Notes |
|------|-----------|-------|
| UI / conversation | `google_sans_flex.ttf` | Weights ~400–850 |
| Code | `google_sans_code.ttf` | Fenced + inline |
| X heritage (citations / some chrome) | Chirp (300–800) | Optional; citations only |

**Licensing:** Google Sans Flex / Code / Chirp are not freely redistributable like Inter. For Grokion:

1. Prefer bundling only if license allows (check shipping policy).
2. Fallback stack: `sans-serif` / Roboto → Inter if we vendor OFL Inter.
3. DESIGN parity means **metrics** (size / weight / leading), not illegally shipping proprietary binaries.

### 5.2 Scale (conversation)

| Role | Size | Weight | Line height | Tracking |
|------|------|--------|-------------|----------|
| Screen title (history) | 28sp | 700 | 1.2 | −0.4sp |
| Section header | 20sp | 700 | 1.25 | −0.3sp |
| Conversation title | 17sp | 600 | 1.3 | −0.2sp |
| Assistant body | 16sp | 400 | **1.55** | 0 |
| User message | 16sp | 400 | 1.45 | 0 |
| Prompt input | 16sp | 400 | 1.4 | 0 |
| Mode / chip label | 14sp | 600 | 1.0 | 0 |
| Citation author | 14sp | 700 | 1.3 | −0.1sp |
| Citation meta | 13sp | 400 | 1.3 | 0 |
| Suggestion chip | 14sp | 400 | 1.5 | 0 |
| Code | 13.5sp mono | 400 | 1.5 | 0 |
| Button | 15sp | 600 | 1.0 | 0 |
| Caption / disclaimer | 12sp | 400 | 1.35 | 0 |
| Label UPPER | 11sp | 700 | 1.2 | +0.6sp |

Appearance settings expose **Text size** scaling (`grok_settings_appearance_text_size`) — support a user text-scale multiplier later; v1 can follow system font scale.

---

## 6. Spacing, radius, elevation

### Spacing (4dp base)

`4, 8, 12, 16, 20, 24, 32, 40, 48`  
Horizontal content margin: **16dp**  
Turn gaps: **24dp** user→assistant, **32dp** between turns  
Composer side margin: **14–16dp**

### Radius

| Element | Radius |
|---------|--------|
| Inline code | 4dp |
| Suggestion chip / citation / code block | 14–16dp |
| User bubble | 20dp (tail corner **6dp** toward sender) |
| Composer | **999dp** stadium (APK/Grokion pill) — iOS teardown also cites ~24dp rounded rect; match **stadium** on Android |
| Mode / new-chat pill | 18dp / full |
| Send | circle 50% |
| Sheets / dialogs | 16–24dp top |

### Elevation

No drop shadows in transcript. Depth = surface value + 1dp border.  
Overlays: scrim `#000000` @ ~60%; history leading shadow only if needed for separation.

---

## 7. Component inventory (1:1)

### 7.1 Top bar

| Spec | Value |
|------|-------|
| Height | 44–56dp + system insets |
| Background | `backgroundHigh`, no blur |
| Leading | History / menu — 2-line hamburger (`ic_grok_menu` style: two strokes), 20–24dp, `foregroundPrimary` |
| Center | Model / mode chip (Grok: personality or model). Pill on `surfaceNeutral`, 13–14sp secondary text |
| Trailing | New chat (`square.and.pencil` / edit) **or** overflow — primary is new chat; Grokion "more" must not look like a dense Material toolbar |
| Scroll divider | 0.5–1dp `border` only when content scrolls under |

### 7.2 History slide-over

| Spec | Value |
|------|-------|
| Width | ~84% viewport |
| Background | `backgroundHigh` |
| Search | placeholder `Search` (`grok_history_search`) |
| Sections | Pinned (`grok_history_pinned_title`), Conversations (`grok_history_conversations_title`), time buckets |
| Row height | ~56dp |
| Title | 16sp primary |
| Meta | 13sp secondary |
| Pressed | `surfaceNeutral` |
| Actions | Pin / Unpin / Delete confirm copy from APK strings |

### 7.3 Transcript — user bubble

| Spec | Value |
|------|-------|
| Align | End / right |
| Max width | ~78–88% |
| Fill | `surfaceNeutral` |
| Border | optional 1dp `border` / hairline |
| Radius | 20dp; bottom-end corner 6dp |
| Text | 16sp / 400 / primary |
| Padding | 12dp vert · 14–16dp horiz |
| No avatar, no name | |

### 7.4 Transcript — assistant

| Spec | Value |
|------|-------|
| Align | Full width, start |
| Chrome | **No bubble** — text on canvas |
| Text | 16sp / 400 / LH 1.55 / primary |
| Leading mark | Optional 24dp Grok glyph above first assistant turn only |
| Markdown | bold weight 700; links `#1D9BF0`; code on `surfaceNeutral` + border |
| Reasoning | Collapsible "Thinking" header, secondary text 13sp (Grokion already close) |
| After stream | Action row: copy · regenerate · share · thumb-up · thumb-down — 18dp glyphs, 44dp hit, 20dp gap, idle = secondary, press = primary, copy success = `#00BA7C` ~1.2s |

### 7.5 Sources / citations (parity target; may stub data)

| Spec | Value |
|------|-------|
| Label | `Sources` (`grok_attribution_posts_header`) — 11sp / 700 / secondary / +0.6 tracking |
| Card | `surfaceBright` or `#1E2126`-class, 16dp radius, 1dp border, 14dp pad |
| Header | avatar 28dp · name 14/700 · verified `#1D9BF0` · `@handle · time` 13/400 secondary |
| Body | 14sp, 4-line clamp |
| Pressed | border → link blue, fill → brighter surface |

### 7.6 Empty state

Centered Grok mark/wordmark, short prompt, **3–4 suggestion chips** (`surfaceNeutral` + border, 14sp, 14dp radius, pad 10×14). No illustrations, no gradients.

### 7.7 Composer

| Spec | Value |
|------|-------|
| Position | Pinned above nav / home indicator |
| Shape | Stadium pill, min height **48dp**, grow to **5 lines** then internal scroll |
| Fill | `surfaceNeutral` (`#181818` dark / `#F2F2F2` light) |
| Border | 1dp `border` |
| Hint | `Ask anything` (`grok_input_ask_anything`) — already in Grokion strings |
| Leading | Attach / `+` menu (Camera, Gallery, Files, …) — idle secondary |
| Trailing send | **32dp circle** (44dp hit) |
| Send disabled | fill `surfaceBright`/`#242424`, arrow secondary |
| Send enabled | fill `#FFFFFF`, arrow `#000000` (use APK `ic_send` path: up chevron) |
| Streaming | morph to stop square on dark circle |
| Focus | border slightly brighter only — **no colored focus ring** |

### 7.8 Streaming motion

- Token reveal left→right (teletype, no per-char bounce)
- Block cursor `▍` primary color, ~530ms on/off
- On complete: cursor fade 200ms → action row fade in
- Send tap: scale 0.92 spring + soft haptic (optional)
- Send↔Stop: 200ms crossfade

### 7.9 Dialogs / sheets / switches

- Sheet / dialog fill: `surfaceBright` / card
- Scrim: black ~60–75%
- Switches: track/thumb monochrome (white track when on, black thumb) — match Grokion switch work to white/black, not orange
- Ripples: soft on `surfaceBright`, not colored accent

### 7.10 Icons (APK)

| Asset | Use |
|-------|-----|
| `ic_send.xml` | Up arrow send |
| `ic_grok_menu.xml` | Two-stroke menu (history/tools) |
| `ic_vector_history.xml` | History |
| `ic_vector_grok_icon.xml` / logo | Empty state / about |
| Wordmarks | `bg_grok_wordmark.xml` etc. — branding only |

---

## 8. Copy (parity strings)

Use these exact user-visible phrases where the control exists:

| Key | Copy |
|-----|------|
| Composer hint | Ask anything |
| New chat | Start new chat |
| History title | Conversations |
| Pinned | Pinned |
| Search | Search |
| Sources | Sources |
| Appearance | Appearance |
| Text size | Text size |
| Preview user | What is the truth of the universe? |
| Preview grok | The universe is a vast system of laws and mysteries. |
| Theme labels | Light · Dark · System · For You |

---

## 9. Gap matrix — Grokion → 1:1

Legend: `Todo` | `Partial` | `Done` | `N/A` (product skip)

Updated after 2026-07-27 Ask UI parity overhaul.

### 9.1 Foundations

| ID | Element | Status |
|----|---------|--------|
| F1–F11, F14 | Semantic colors, links, errors, success, borders, sunset demotion | Done |
| F12 | System/Roboto metrics (LH 1.55 etc.) | Done |
| F13 | Light / Dark / System | Done |

### 9.2 Shell chrome

| ID | Element | Status |
|----|---------|--------|
| S1–S4 | Sparse top bar, grok menu, model chip, new chat | Done |
| S5–S6 | 84% Conversations panel + search + 0.6 scrim | Done |
| S5b | Pinned + settings gear; time buckets; title-only rows | Done |
| S7 | Ask/Imagine tabs | N/A |

### 9.3 Transcript

| ID | Element | Status |
|----|---------|--------|
| T1–T4, T6–T10 | Bubble tail, LH, cursor, copy green, empty mark, stubs, reasoning | Done |
| T5 | Action row (copy/share/edit/tts; thumbs/regen omit) | Done |

### 9.4 Composer

| ID | Element | Status |
|----|---------|--------|
| C1–C6, C8 | Pill, hint, send states, stop morph, monochrome focus | Done |
| C7 | Attach sheet (Camera/Gallery/Files + system/tools) | Done |

### 9.5 Settings / secondary

| ID | Element | Status |
|----|---------|--------|
| X1, X4, X5 | Appearance preview, mono switches, dialog scrim | Done |
| X0 | Settings root IA (Appearance/Voice/Haptics/Models/Advanced/Data) | Done |
| X2 | Settings text-size slider | N/A (in-chat ± remains) |
| X3 | Haptics prefs | Done |

### 9.6 Motion (2026-07-27 audit)

| ID | Element | Status |
|----|---------|--------|
| M1 | Fragment stack slide (300/280ms ease-out) | Done |
| M2 | History drawer ease + cancel + reduced-motion | Done |
| M3 | Send↔Stop 200ms morph | Done |
| M4 | Overflow menu fade + no layoutChanges fight | Done |
| M5 | Bottom sheet theme (16dp top, 0.6 scrim) | Done |

See also [`SHELL.md`](SHELL.md) for remap/omit/gates.

## 10. Implementation order

1. **Tokens** — rewrite `values/colors.xml` + `values-night/colors.xml` to §4; remap all `xai_*` usages; delete sunset-as-accent wiring.
2. **Type** — assistant/user/prompt textAppearance: 16sp, weights, LH 1.55; optional font family.
3. **Composer send** — white/black circle + `ic_send` path parity; stop morph.
4. **Bubbles / transcript** — tail radius, leading, action row, copy green.
5. **Streaming cursor**.
6. **Empty state chips**.
7. **History panel** polish (search, pinned section chrome).
8. **Sources/citation layouts** (UI shells even if data stubs).
9. **Appearance** screen preview (optional).
10. Side-by-side device check vs installed `ai.x.grok`.

---

## 11. Android resource mapping (suggested)

```text
xai_canvas              → backgroundHigh
xai_canvas_soft         → surfaceNeutral / backgroundLow
xai_canvas_card         → surfaceBright (menus/sheets)
xai_ink                 → foregroundPrimary
xai_mute                → foregroundSecondary
xai_body                → foregroundPrimary (or secondary for meta)
xai_hairline / border   → border (alpha)
xai_link                → #1D9BF0
xai_error               → foregroundDestructive
xai_accent_sunset       → REMOVE from chrome; alias to secondary or white if needed for compile
```

Drawables to align: `bg_composer_pill`, `bg_user_message`, `bg_top_bar_grok`, send/stop, menu, switches.

Reference-only (gitignored): `references/grok_decompiled`, `references/grok_base.apk`.

---

## 12. Do / Don't (parity)

**Do**

- Ship Dark as `#050505` void with greyscale surfaces.
- Assistant = plain full-width text.
- White filled send when enabled; monochrome focus.
- Link blue only on links/citations.
- Pin composer; history as slide-over.

**Don't**

- Soft Material dark `#121212` as canvas.
- Orange / purple / "sunset" decorative accents on Ask UI.
- Assistant chat bubbles.
- Colored body emphasis (use weight).
- Drop shadows in the transcript.
- Bottom Imagine tab (unless product expands).
- Treat x.ai marketing DESIGN.md as chat truth.

---

## 13. Verification

Before calling parity done:

1. Dark Ask screen screenshot vs `ai.x.grok` on same device (canvas, composer, send, bubble).
2. Light Ask screen same.
3. Gap matrix §9 all `Todo`/`Partial` → `Done` or explicit `N/A`.
4. No remaining `xai_accent_sunset` on interactive chrome.
5. Link color in markdown = `#1D9BF0`.

---

*Extracted tokens: `GrokSemanticColors` / greyscale from `k9.h`+`k9.i`; Horizon + link/success from `j80`/`e80.e0`; fonts from `res/font`; strings from `values/strings.xml`; package `ai.x.grok` `1.1.97-release.01`.*
