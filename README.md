# Grokion

Android LLM chat client. Fork of [oxproxion](https://github.com/stardomains3/oxproxion).

- **applicationId:** `io.github.warexpor.grokion`
- **minSdk / targetSdk:** 31 / 36
- **License:** Apache 2.0 (see `LICENSE`)
- **Upstream credit / support:** [buymeacoffee.com/oxproxion](https://www.buymeacoffee.com/oxproxion)

Not affiliated with OpenRouter.ai or xAI.

## Backends

| Backend | Transport | Notes |
|---------|-----------|--------|
| [OpenRouter](https://openrouter.ai/) | HTTPS | Requires API key + credits |
| Ollama, LM Studio, llama.cpp, MLX LM, Hermes Agent | LAN HTTP(S) | User-hosted; app does not provision servers |

HTTP LAN endpoints are restricted to private, loopback, link-local, or `.local` hosts. HTTPS may target any host. Self-signed LAN TLS requires **Trust self-signed LAN TLS** in Settings (default off).

## Capabilities

- Streaming and non-streaming chat; vision and image generation where the model supports them
- System messages, presets (also share targets), reasoning controls, custom fonts
- Saved sessions with import/export; optional auto-save
- On-device PDF of a message or full chat (Android PdfDocument)
- Tool calling (workspace file R/W, calendar, timer/alarm); tools default off; destructive file tools gated in Settings
- Workspace: `Download/grokion` preferred; legacy `Download/oxproxion` still readable
- Conversation mode (STT/TTS); share / assistant / spell-check entry points
- Markwon markdown rendering; OpenRouter credits / model info from the model list UI

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

Current marketing version: see `versionName` in `app/build.gradle.kts` and `CHANGELOG.md`.

## Configuration

1. **OpenRouter:** Settings → OpenRouter API Key.
2. **LAN:** Settings → LAN Settings (base URL + optional API key). Configure the server yourself (bind address, auth, firewall). Hermes Agent needs its API server enabled (`API_SERVER_HOST` reachable from the device, `API_SERVER_KEY` if set).

In-app Help covers UI details, presets, and Hermes setup. Upstream release notes: [stardomains3/oxproxion/releases](https://github.com/stardomains3/oxproxion/releases). This fork: [Warexpor/oxproxion/releases](https://github.com/Warexpor/oxproxion/releases).

## Privacy / security (summary)

- No trackers, analytics, or ads in this app
- Chat history stays on device; encrypted at rest (SQLCipher, Keystore-wrapped passphrase)
- OpenRouter and model-provider privacy/pricing policies apply to cloud traffic; LAN HTTP is cleartext on the local network unless you use HTTPS
- Intended for users 18+
- Provided as-is, without warranty
