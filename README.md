# Grokion

Android LLM chat client. Fork of [oxproxion](https://github.com/stardomains3/oxproxion).

- **applicationId:** `io.github.warexpor.grokion`
- **minSdk / targetSdk:** 31 / 36
- **License:** Apache 2.0 (see `LICENSE`)
- **Upstream credit / support:** [buymeacoffee.com/oxproxion](https://www.buymeacoffee.com/oxproxion)

**Unofficial / personal project.** Not affiliated with, endorsed by, or sponsored by xAI, Grok, or OpenRouter.ai. The name and UI resemblance are for personal use only; this is not an official Grok client and must not be treated as one.

## Backends

| Backend | Transport | Notes |
|---------|-----------|--------|
| [OpenRouter](https://openrouter.ai/) | HTTPS | Requires API key + credits |
| Ollama, LM Studio, llama.cpp, MLX LM, Hermes Agent | LAN HTTP(S) | User-hosted; app does not provision servers |

HTTP LAN endpoints are restricted to private, loopback, link-local, or `.local` hosts. HTTPS may target any host. Self-signed LAN TLS requires **Trust self-signed LAN TLS** in Settings → Models & API (default off).

## UI

Grok-style Ask shell (sparse top bar, composer pill, history drawer, settings cards). App font is Inter; history wordmark uses Iceland. Secondary screens (libraries, model catalogs, dialogs) share the same canvas chrome.

Design contracts: [`DESIGN.md`](DESIGN.md), [`SHELL.md`](SHELL.md).

## Capabilities

- Streaming and non-streaming chat; vision and image generation where the model supports them
- System messages, presets (also share targets), reasoning controls
- Conversations **always autosave** to History; rename from History; import/export from History or Settings → Data & Privacy
- One-chat forks: edit / resend / delete can stash an alternate branch and restore it in the same chat
- On-device PDF of a message or full chat (Android PdfDocument)
- Tool calling (workspace file R/W, calendar, timer/alarm); tools default off; destructive file tools gated in Settings
- Workspace: `Download/grokion` preferred; legacy `Download/oxproxion` still readable
- TTS and transcription-model file upload remain; **live STT (mic / Voice settings) is disabled**
- Markwon markdown rendering; OpenRouter credits / model info from the model list UI
- Notifications: “answer ready” only (toasts silenced)

## Stack

| Layer | Choice |
|-------|--------|
| Language | Kotlin |
| UI | Fragments, View Binding, Material 3 |
| Architecture | MVVM (`ViewModel`, LiveData / StateFlow), Coroutines |
| Persistence | Room + SQLCipher (`chat_database`); API keys via Android Keystore AES-GCM |
| Network | Ktor (OkHttp engine); SSE/NDJSON for streams |
| Markdown | Markwon + Prism4j |

Cloud backup / device transfer excludes `ApiKeysPrefsStore`, main prefs, and the chat DB sidecars.

## Build

```bash
git clone https://github.com/Warexpor/oxproxion.git Grokion
cd Grokion
./gradlew :app:assembleDebug
```

Open the project in Android Studio (AGP 8.13+, JDK 17) and run the `app` configuration.

Current marketing version: see `versionName` in `app/build.gradle.kts` and `CHANGELOG.md` (currently `2.1.128-grokion`).

## Configuration

1. **OpenRouter:** Settings → Models & API → OpenRouter API Key.
2. **LAN:** Settings → Models & API → LAN (base URL + optional API key). Configure the server yourself (bind address, auth, firewall). Hermes Agent needs its API server enabled (`API_SERVER_HOST` reachable from the device, `API_SERVER_KEY` if set).

In-app Help covers UI details, presets, and Hermes setup. Upstream release notes: [stardomains3/oxproxion/releases](https://github.com/stardomains3/oxproxion/releases). This fork: [Warexpor/oxproxion/releases](https://github.com/Warexpor/oxproxion/releases).

## Privacy / security (summary)

- No trackers, analytics, or ads in this app
- Chat history stays on device; encrypted at rest (SQLCipher, Keystore-wrapped passphrase)
- OpenRouter and model-provider privacy/pricing policies apply to cloud traffic; LAN HTTP is cleartext on the local network unless you use HTTPS
- Intended for users 18+
- Provided as-is, without warranty
