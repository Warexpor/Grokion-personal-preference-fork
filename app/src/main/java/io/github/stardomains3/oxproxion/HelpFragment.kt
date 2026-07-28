package io.github.stardomains3.oxproxion

import io.github.stardomains3.oxproxion.Motion.withGrokStackAnimations

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import kotlin.math.roundToInt
import androidx.core.net.toUri

class HelpFragment : Fragment(R.layout.fragment_help) {
    private lateinit var folderPickerLauncher: ActivityResultLauncher<Uri?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folderPickerLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            if (uri != null) {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
                SharedPreferencesHelper(requireContext()).saveSafFolderUri(uri.toString())
                AppToast.makeText(requireContext(), "Folder updated! Tools should now work.", AppToast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
         val dp24 = with(requireContext().resources.displayMetrics) {
            (24 * density).roundToInt()
        }
         val icons = mapOf(
            "ic_send" to R.drawable.ic_send,
            "ic_stop" to R.drawable.ic_stop,
            "ic_attachdoc" to R.drawable.ic_attachdoc,
            "ic_imgup" to R.drawable.ic_imgup,
            "ic_palette" to R.drawable.ic_palette,
            "ic_rechat" to R.drawable.ic_rechat,
            "ic_tune" to R.drawable.ic_tune,
            "ic_schats" to R.drawable.ic_schats,
            "ic_new_chat" to R.drawable.ic_new_chat,
            "ic_savechat" to R.drawable.ic_savechat,
            "ic_copi" to R.drawable.ic_copi,
            "ic_markdown" to R.drawable.ic_markdown,
            "ic_html" to R.drawable.ic_html,
            "ic_epub" to R.drawable.ic_epub,
            "ic_print" to R.drawable.ic_print,
            "ic_pdfnew" to R.drawable.ic_pdfnew,
            "ic_stream" to R.drawable.ic_stream,
             "ic_tools" to R.drawable.ic_tools,
            "ic_reasoning" to R.drawable.ic_reasoning,
            "ic_ruler" to R.drawable.ic_ruler,
            "ic_key" to R.drawable.ic_key,
            "ic_notinew" to R.drawable.ic_notinew,
            "ic_scrollers" to R.drawable.ic_scrollers,
            "ic_extend" to R.drawable.ic_extend,
            "ic_websearch" to R.drawable.ic_websearch,
            "ic_paste" to R.drawable.ic_paste,
            "ic_mic" to R.drawable.ic_mic,
            "ic_convo" to R.drawable.ic_convo,
            "ic_fingerprint" to R.drawable.ic_fingerprint,
            "ic_presets" to R.drawable.ic_presets,
            "ic_fonts" to R.drawable.ic_fonts,
             "ic_format" to R.drawable.ic_format,
            "ic_backlight" to R.drawable.ic_backlight,
             "ic_lan" to R.drawable.ic_lan,
             "ic_menudot" to R.drawable.ic_menudot,
             "ic_prompts" to R.drawable.ic_prompts,
             "ic_settings" to R.drawable.ic_settings,
             "backcopy" to R.drawable.backcopy,
             "backtoapp" to R.drawable.backtoapp
        )

         val iconSpans: Map<String, ImageSpan> = icons.mapValues { (_, resId) ->
            val drawable = ContextCompat.getDrawable(requireContext(), resId)!!.mutate().apply {
                setBounds(0, 0, dp24, dp24)
            }
            ImageSpan(drawable)
        }

        val helpContentTextView = view.findViewById<TextView>(R.id.helpContentTextView)
        val sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        val selectedFontName = sharedPreferencesHelper.getSelectedFont()
        val typeface = AppFonts.resolveSelectable(requireContext(), selectedFontName)
        val versionName = getAppVersionName(requireContext())
        helpContentTextView.typeface = typeface
        val markwon = Markwon.builder(requireContext())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES))
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(CoilImagesPlugin.create(requireContext()))
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder
                        .codeTextColor(Color.LTGRAY)
                        .codeBackgroundColor(Color.argb(128, 0, 0, 0))
                        .codeBlockBackgroundColor(Color.argb(128, 0, 0, 0))
                        .blockQuoteColor(Color.BLACK)
                        .isLinkUnderlined(true)
                }
            })
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun afterSetText(textView: android.widget.TextView) {
                    val spannable = textView.text as? Spannable ?: return
                    val spans = spannable.getSpans(0, spannable.length, ClickableSpan::class.java)
                    for (span in spans) {
                        val start = spannable.getSpanStart(span)
                        val end = spannable.getSpanEnd(span)
                        val text = spannable.subSequence(start, end).toString()
                        if (text.contains("re-select")) {
                            spannable.removeSpan(span)
                            spannable.setSpan(object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    val downloadsUri =
                                        "content://com.android.externalstorage.documents/document/primary%3ADownload".toUri()

                                    folderPickerLauncher.launch(downloadsUri)
                                }
                            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
            })
            .build()


        val markdownContent = """
            # GradatiON Help

            **App version: $versionName**

            GradatiON is a personal Android client for OpenRouter and LAN-hosted models (Ollama, LM Studio, llama.cpp, MLX LM, Hermes Agent). It is a fork of [oxproxion](https://github.com/stardomains3/oxproxion).

            ---

            ## Getting started

            1. Add an [OpenRouter](https://openrouter.ai/) API key in **Settings → AI → Models & API** (or use LAN models only).
            2. Tap the **model name** on the chat screen to pick or add models.
            3. Chats **autosave** to **History** {{ic_schats}} as you talk.

            **Repo:** [github.com/Warexpor/Grokion-personal-preference-fork](https://github.com/Warexpor/Grokion-personal-preference-fork) · **Upstream:** [stardomains3/oxproxion](https://github.com/stardomains3/oxproxion)

            ---

            ## Chat screen

            ### Top bar
            *   **History** {{ic_schats}} — conversations, search, pin, rename, delete, import/export.
            *   **Model chip** {{ic_tune}} — switch models, browse OpenRouter/LAN catalogs.
            *   **New chat** {{ic_new_chat}} — starts a fresh thread (current chat stays in History).
            *   **Menu** {{ic_menudot}} — overflow tools (copy/export chat, reasoning, web search, stream, tools, presets, settings).

            ### Composer
            *   **Send** {{ic_send}} / **Stop** {{ic_stop}}
            *   **Attach** {{ic_attachdoc}} — text files. **Image** {{ic_imgup}} — vision models (camera long-press).
            *   **+** — attach sheet (camera, gallery, files, tools manager).

            ### Message actions
            Tap icons under a message to copy, share, speak, export (PDF/Markdown/HTML/PNG), edit, or resend. **Edit/resend** can fork the thread — use the `< n/m >` navigator to switch branches.

            ---

            ## Overflow menu

            *   **Reasoning** {{ic_reasoning}} — for supported models; long-press for advanced options.
            *   **Web search** {{ic_websearch}} — one-shot for OpenRouter models (long-press for engine).
            *   **Stream** {{ic_stream}} — streaming on/off.
            *   **Tools** {{ic_tools}} — experimental; long-press to choose tools. Workspace: **Download/gradation** (legacy **grokion** / **oxproxion** still readable). [Re-select folder](action://reselect-folder) if tools cannot read files.
            *   **Presets** {{ic_presets}} — saved model/settings bundles; also a share target.
            *   **Settings** {{ic_settings}} — appearance, haptics, advanced options, models/API, data & privacy.

            ---

            ## Settings

            Open from History (gear) or the overflow menu.

            | Section | What’s inside |
            |---------|----------------|
            | **App → Appearance** | Theme, fonts, extended toolbar, scroll helpers |
            | **App → Haptics** | Button and response haptics |
            | **App → Advanced** | Biometrics, notifications, presets on chat, LAN, tokens, API keys, **Help** (this guide) |
            | **AI → Models & API** | OpenRouter key, LAN endpoints, trust self-signed TLS |
            | **AI → Data & Privacy** | Import/export, destructive file tools, workspace |

            ---

            ## Models & History

            *   **Your Models** — tap model chip; add from OpenRouter or LAN catalogs; filters and sort at top.
            *   **History** — autosaved threads; overflow per row for pin/rename/delete; bottom bar for search, settings, new chat.
            *   **System messages & prompts** — from overflow or long-press Presets; libraries support import/export.
            *   Generated images may not persist when reopening a saved chat.

            ---

            ## Image & audio models

            *   **Image generation** — palette badge on model; images save to Downloads; not kept in History.
            *   **Transcription** — upload audio via mic icon when a transcription model is selected; returns text only. **Live voice/STT in Settings is disabled** in this build.

            ---

            ## Hermes Agent (LAN)

            Hermes Agent v0.4.0+ with API server enabled (`API_SERVER_HOST=0.0.0.0`, optional `API_SERVER_KEY`). Add **hermes-agent** from the LAN catalog. Text chat only — Hermes runs its own tools on the host. Docs: [hermes-agent API server](https://github.com/NousResearch/hermes-agent/blob/main/website/docs/user-guide/features/api-server.md).

            ---

            ## Privacy & tips

            *   No trackers, analytics, or ads.
            *   Chats stay on device (SQLCipher + Keystore). Export before uninstall if you need backups.
            *   OpenRouter/LAN traffic and pricing are your responsibility — check provider policies.
            *   HTTP LAN is cleartext on your network unless you use HTTPS (+ optional self-signed trust in Settings).
            *   Notifications: **“Your answer is ready”** only when backgrounded; dismissed when you return to the app.
            *   Variable substitution in prompts: `{{oxdate}}`, `{{oxtime}}`, `{{oxdatetime}}`, `{{oxhdt}}`.
            *   Apache 2.0 · 18+ · provided as-is.

            **Third-party licenses:** [View in app](oxproxion://licenses)

        """.trimIndent()

        markwon.setMarkdown(helpContentTextView, markdownContent)
        val text = helpContentTextView.text
        val spannable1 = SpannableStringBuilder(text)
        iconSpans.forEach { (name, span) ->
            val placeholder = "{{$name}}"
            var start = 0
            while (true) {
                start = spannable1.indexOf(placeholder, start)
                if (start == -1) break
                val end = start + placeholder.length
                spannable1.replace(start, end, " ")  // Single space for icon slot
                spannable1.setSpan(span, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                start += 1
            }
        }
        helpContentTextView.setText(spannable1, TextView.BufferType.SPANNABLE)
        helpContentTextView.movementMethod = LinkMovementMethod.getInstance()
        // Custom handler for licenses link
        val spannable = helpContentTextView.text as? Spannable ?: return
        val urlSpans: Array<URLSpan> = spannable.getSpans(0, spannable.length, URLSpan::class.java)

        for (urlSpan in urlSpans) {
            if (urlSpan.url == "oxproxion://licenses") {
                val start = spannable.getSpanStart(urlSpan)
                val end = spannable.getSpanEnd(urlSpan)
                val flags = spannable.getSpanFlags(urlSpan)
                spannable.removeSpan(urlSpan)

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        parentFragmentManager.beginTransaction()
                            .withGrokStackAnimations()
                            .hide(this@HelpFragment)  // ← Matches your pattern: hide current (HelpFragment)
                            .add(R.id.fragment_container, LicenseListFragment())
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
                        ds.isUnderlineText = true
                    }
                }
                spannable.setSpan(clickableSpan, start, end, flags)
            }
        }
        helpContentTextView.text = spannable
        helpContentTextView.isClickable = true

    }
    // Add this helper function (e.g., in your Activity/Fragment or a Utils class)
    private fun getAppVersionName(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

}