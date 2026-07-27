# Grokion Ask shell contract

**Goal:** Grok Ask (+ History + Settings) navigation/layout + oxproxion backend for local/LAN/OpenRouter.  
**Stack:** Views/Fragments. Pref keys unchanged unless migrated.  
**Rule A:** Real-capability rows only — no SuperGrok / account stubs.

Reference: `ai.x.grok` 1.1.97 (`references/grok_decompiled`), [`DESIGN.md`](DESIGN.md).

---

## 1. Screen inventory

| Screen | Host | Notes |
|--------|------|-------|
| Ask | `ChatFragment` | Top: menu · model chip · new chat · more; composer pill; empty mark |
| History drawer | `SavedChatsFragment` embedded | ~84% width; Search; Pinned; Conversations; Settings gear |
| Settings root | `SettingsFragment` | Section list → push sub-screens |
| Appearance | `SettingsDetailFragment` (appearance) | Theme + preview |
| Haptics | section | Button + responding toggles |
| Models & API | section | LAN, keys, credits, trust TLS |
| Advanced | section | Libraries / Generation / Chat chrome (power tools bar) |
| Data & Privacy | section | Biometrics, notifications, import/export, help, licenses |
| Model picker | `BotModelPickerFragment` | Primary model entry (chip) |
| Attach sheet | popup from composer `+` | Camera / Gallery / Files (+ Tools); Gallery always opens system picker |
| Libraries / catalogs | Advanced children | Tools, prompts, presets, system messages, OR/LAN catalogs — canvas chrome |

Voice settings entry is disabled (STT removed). Conversations always autosave to History.

---

## 2. Remap table (oxproxion → Grok sections)

### Appearance
| Pref / UI | Destination |
|-----------|-------------|
| `themeToggleGroup` | Appearance |
| `appearancePreviewCard` | Appearance |
| In-chat font ± (`getFontSize`) | Appearance (display) + existing chat controls |

### Voice
| Pref / UI | Destination |
|-----------|-------------|
| (disabled) | STT removed from product |

### Haptics
| Pref / UI | Destination |
|-----------|-------------|
| `haptic_buttons` (new) | Haptics |
| `haptic_responding` (new) | Haptics |

### Models & API
| Pref / UI | Destination |
|-----------|-------------|
| `lanButton` / `SaveLANDialogFragment` | Models & API |
| `trustSelfSignedLanSwitch` | Models & API |
| `apiKeyButton` | Models & API |
| `braveApiKeyButton` | Models & API |
| `creditsButton` | Models & API |
| Model catalogs | Chip primary; optional row → picker |

### Advanced
| Pref / UI | Destination |
|-----------|-------------|
| `toolsButton` / `ToolsFragment` | Advanced |
| `promptsButton` / Prompt library | Advanced |
| Presets list | Advanced |
| System messages | Advanced (or attach sheet) |
| `inferenceParamsButton` | Advanced |
| `maxTokensButton` | Advanced |
| `timeoutButton` | Advanced |
| `chatMemoryButton` | Advanced |
| Advanced Reasoning | Advanced + long-press |
| `openRouterTransformsSwitch` | Advanced |
| `autoDisableWebSearchSwitch` | Advanced |
| `extendedDockSwitch` / `extendedTopBarSwitch` | Advanced → **Show power tools bar** (single switch drives both) |
| `expandableInputSwitch` | Advanced |
| `scrollButtonsSwitch`, `scrollProgressSwitch`, `volumeScrollSwitch` | Advanced |
| `presetsExtendedSwitch`, `animateBarOnErrorSwitch`, `showCitationsSwitch` | Advanced |

### Data & Privacy
| Pref / UI | Destination |
|-----------|-------------|
| `biometricsSwitch` | Data & Privacy |
| Autosave (always on; no toggle) | Data & Privacy (behavior only) |
| `notificationsSwitch`, copy/open/dismiss | Data & Privacy |
| `allowDestructiveToolsSwitch` | Data & Privacy |
| `keepScreenOnSwitch` | Data & Privacy |
| Help, licenses | Data & Privacy |
| History import/export buttons | Data & Privacy (+ History bottom bar) |

---

## 3. Omit list (Grok cloud / non-local)

SuperGrok, Sign out, Delete Account, Kids Mode, Connectors, Skills marketplace, Usage billing, Shared Conversations, NSFW Preferences, cloud Memory, Automations/Tasks, Imagine tab, LiveKit pipeline, Team workspace.

---

## 4. Screenshot gates (done criteria)

1. Empty Ask — dark + light  
2. Streaming reply + action row  
3. History open (search + list + settings gear)  
4. Settings root  
5. Appearance / Advanced / Data sub-screens (card IA)  
6. Model picker sheet  
7. Composer attach sheet  

Plus: LAN/local select → stream → stop → new chat → pin/search → theme change → History import/export.
