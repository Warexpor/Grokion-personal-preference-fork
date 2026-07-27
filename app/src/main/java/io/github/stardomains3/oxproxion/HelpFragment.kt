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
            # Grokion Help Guide
            
             **App Version: $versionName**

            Welcome! This guide will help you understand how to use the **Grokion** app.

            **Grokion** is a fork of [oxproxion](https://github.com/stardomains3/oxproxion) — an open-source Android app for chatting with OpenRouter LLMs, supporting both text and image inputs for compatible models. It also allows chat with Ollama, LM Studio, llama.cpp, MLX LM and Hermes Agent models served on your LAN.

            ---

            ## 🚀 Getting Started

            To use the app, you need an **OpenRouter API key with credit**.
            *   Get and fund your key at: <br>[https://openrouter.ai/](https://openrouter.ai/)
            <!---->
            *   Find model info and pricing at: <br>[https://openrouter.ai/models/](https://openrouter.ai/models/)

            ---

            ## 🔗 Important Links

            *   **Grokion (this fork)**: <br>[https://github.com/Warexpor/oxproxion](https://github.com/Warexpor/oxproxion)
            <!---->
            *   **Upstream oxproxion**: <br>[https://github.com/stardomains3/oxproxion](https://github.com/stardomains3/oxproxion)
            <!---->
            *   **Upstream F-Droid**: <br>[oxproxion](https://f-droid.org/en/packages/io.github.stardomains3.oxproxion/)
            <!---->
            *   **Support the Dev**(I could use it): <br>[https://www.buymeacoffee.com/oxproxion](https://www.buymeacoffee.com/oxproxion) ☕
            <!---->
           
            ---

            ## ✨ Core Features

            *   **Multi-Model Support**: Easily switch between different LLM models.
            *   **History & Autosave**: Conversations save automatically to History as you chat.
            *   **Import & Export**: Back up or restore chat history from History (Import/Export) or Settings → Data & Privacy.
            *   **Streaming Responses**: Choose between real-time streaming or full responses.
            *   **Chat with Images**: Use models that support vision.
            *   **Generate Images**: Use models that support it.
            *   **System Messages**: Customize the AI's behavior and persona.
            *   **Modern Tech**: Built for Android using native tools like Kotlin, Jetpack, Coroutines, and Ktor.

            ---

            ## 📱 Main Chat Interface

            ### Top Bar (Grok Ask shell)
            *   **Model chip** {{ic_tune}} : Tap the **model name** at the top to open model selection and switch models.
            *   **Menu** {{ic_menudot}} : Opens the overflow menu (copy chat, export formats, reasoning, web search, stream, tools, presets, settings, and more).
            *   **History** {{ic_schats}} : Opens the **History** drawer with your saved conversations. Search, pin, rename, or delete from the overflow on each row.
            *   **New chat** {{ic_new_chat}} : Starts a fresh conversation. Your current chat stays in History. Long-press to skip the confirmation dialog.

            ### Interacting with Messages
            *   **Copy AI Response**: Tap the **robot icon** to copy the AI's message. Long Press to copy Markdown RAW.
            *   **Copy User Message**: Tap the **user icon** to copy your message.
            *   **Share AI Response**: Tap the **share icon** to send the AI's text to other apps. Long-press to share the raw markdown of the response.
            *   **Speak AI Response**: Tap the **speaker icon** to speak out loud the AI's response(Up to 3900 characters.) Will not display if your device's text-to-speech engine isn't available. Long-press to save an audio wav file of the AI's response to your downloads folder. (Made on device using Android tools. Generation usually done in seconds.)
            *   **Create PDF of Response**: Tap the **pdf icon** to save just that response as a PDF in your device's Downloads folder. Long-press for .jpg.
            *   **Create Markdown File of Response**: Tap the **Markdown icon** to save just that response as a .md file in your device's Downloads folder. Long-press to save a .txt file in your device's Downloads folder.
            *   **Save File of Response**: Tap the **Save File icon** to save just that response as a file in your device's Downloads folder. A dialog will appear asking for the name and extension of the file to be saved.
            *   **Create PNG File of Response**: Tap the **PNG icon** to save just that response as a .png file in your device's Downloads folder. Long-press for .webp. 
            *   **View HTML of Response**: Tap the **HTML icon** to view the AI response in HTML in an Android Webview. This makes code blocks easier to read, allows one tap copy of them and other view improvements. A print option appears in the screen too.
            *   **Save HTML of Response**: Long-Press the **HTML icon** to save the AI response as an HTML file in your device's Downloads folder.
            *   **Edit User Message**: Tap the **edit icon** on a user message to load its text into the input box for editing. Caution: this removes the message and all subsequent messages from the history.
            *   **Resend User Message**: Tap the **resend icon** on a user message to resend the prompt and generate a new response. Caution: this removes all messages after it while keeping the original prompt.

            ### Sending Prompts
            *   **Text Box**: Enter your prompt.
            *   **Send Button** {{ic_send}} : Send your prompt to the LLM. **Long-press** will go to latest message when long pressed.
            *   **Stop Button** {{ic_stop}} : During an api call, tap the Stop Button to end the api call.
            *   **Attach Document(s) Button** {{ic_attachdoc}} : Click icon to attach **text-based only** files to send with your prompt. Long-press to see what's attached and/or detach them.
            *   **Image Button** {{ic_imgup}} : Enabled for vision models. Click icon to attach a single image or take picture up to 12MB in size. Long-click goes straight to camera. Also, PDF pages. Select a PDF, then select a page to send(if single page, no page selection appears.) Uses on-device native Android tools to convert to data for the vision model. Select additional pages in following rounds of the chat. Note: if the page has a white background the conversion may make that transparent, but this shouldn't be an issue with the vision model; It just may look different in the image preview you see.
            *   **Palette Button** {{ic_palette}} : If using Nano-Banana, tap to set aspect ratio of returned generated image.
            
            ---
            
            ##  Menu

            Tap the **menu button** {{ic_menudot}} in the top bar to open the overflow menu.
            If your prompt is filled with spelling/grammar errors you can long-press the menu button to send the prompt to model "google/gemma-4-26b-a4b-it" to fix it and it will automatically correct the prompt in the prompt box.

            ### Contextual Buttons (Enabled during a chat)
            *   **Copy Chat** {{ic_copi}} : Copies the full conversation to your clipboard. Long Press to copy Markdown RAW.
            *   **Save to Markdown** {{ic_markdown}} : Saves the full conversation to a markdown file in your downloads folder. Long-press to save to .txt.
            *   **Save to HTML** {{ic_html}} : Saves the full conversation to an HTML file in your downloads folder.
            *   **Save to EPUB** {{ic_epub}} : Saves the full conversation to an EPUB file in your downloads folder. Note: Chats with large tables will not render well in EPUB readers.
            *   **Print Chat** {{ic_print}} : Prints the full conversation. You can save as PDF here too. You can select page size too.
            *   **PDF Button** {{ic_pdfnew}} : Creates a PDF of the entire chat in your downloads folder.
            
            ### Standard Buttons
            *   **Reasoning Button** {{ic_reasoning}} : Appears only for models that support reasoning. Toggles reasoning on or off. When enabled, the model uses deeper thinking for more thoughtful responses. When disabled, it explicitly instructs the model not to use reasoning. Reasoning traces are not returned in the response. Defaults to medium effort. If you long-press on the button if it is selected, you can enable Advanced Reasoning settings(see below). 
            *   **Web Search Button** {{ic_websearch}} : Enables web search(model gets web search information) for **one response only** (auto-off unless overrided with setting, OpenRouter models only). Long-press to choose engine: [**Default** (native if available, else Exa), **Native only**, **Exa only**].  **Note**: Exa is OpenRouter's search provider. Native is server-side of provider(OpenAI, xAI, Anthropic, etc.) Also available: Parallel and Firecrawl. User chooses context size and max results too. Check OpenRouter/provider's docs for pricing — can be expensive! 
            *   **Stream Button** {{ic_stream}} : Toggles streaming responses on or off.
            *   **Tools Button**  {{ic_tools}} : **Experimental, use at own risk!**. Many models do not run tools well/correctly. Exit app by swiping upwards if model misuses/repeats etc tools. Toggles tools on/off. Long press to see available tools: There you can toggle which tools you want available to the model. Available tools are: create file, set timer, brave search,  set alarm, add calendar event, list file in folder, open file, delete file(s), get location, find nearby places, read file in folder. Not all models support tool use. It is recommended to have tools on only when wanted as if adds to your input token count. When you first press this button it will ask which folder to use for reading and listing files; It is recommended you select grokion folder that is in Download folder as that where create file tool makes sends created files. For now only 9 tool uses can be used sequentially.
            *   **Conversation Button** {{ic_convo}} : Toggles "Audio Conversation" mode on or off. When enabled, Speech-to-Text automatically sends recognized prompts to the model, and responses are automatically read aloud via Text-to-Speech.
            *   **Fonts Button** {{ic_fonts}} : Opens the fonts dialog where you can choose System Default or Inter for the main chat screen.
            *   **Font Size Button** {{ic_format}} : Makes visible the font resizing buttons for to change the size of the font in the main chat screen.
            *   **Presets Button** {{ic_presets}} : Opens the Presets screen.
            *   **Settings Button** {{ic_settings}} : Opens the Settings screen.
            *   **Paste Button** {{ic_paste}} : Pastes the contents of your clipboard to the prompt box; When long-pressed, pastes the clipboard to the prompt box and auto-sends it to your selected model. (This button only appears when extended dock is on.)
            *   **Speech-to-Text Button** {{ic_mic}} : Appears when the prompt box is empty, or a Clear Prompt button when the prompt box has some text. (This button only appears when extended dock is on.)
            *   **Back to App Button** {{backtoapp}} : Returns to the previous app. Appears when notification clicked or text shared to Preset target. Long-press: clears chat and returns to the previous app.
            *   **Copy/Back to App Button** {{backcopy}} : Copies last response and returns to the previous app.  Appears when notification clicked or text shared to Preset target. Long-press: copies response, clears chat and returns to the previous app.
           
            ### Settings Screen
            *   **Power tools bar**: Shows the extended dock accessories and top tool strip together.
            *   **Scroll Progress Indicator** : It gives you an indication where you are in the chat.
            *   **Scroll Buttons on screen** {{ic_scrollers}} : If toggled on, shows up and down buttons to scroll on the chat screen. Tap them to go to top or bottom of chat respectively. Long-press them to scroll one screen's length in respective direction.
            *   **Volume Keys Scroll to Top/Bottom** : On the main chat screen, if this setting is enabled, single click of volume button up/down takes you one page up/down respectively. Long-click of volume button up/down takes you to chat very top/bottom respectively. If no messages are present it will work as a normal volume key.  
            *   **Presets Button on chat screen** {{ic_presets}} : Option to have Preset button in menu or on chat screen. Presets enable the user to have pre-selected settings applied to the app: model, system message, reasoning on/off, streaming on/off, and conversation mode on/off, with one tap. These are also exposed as a share target, "Presets", when sharing text to the app; thus making functions like summarization, spelling correction, audio reply, etc with different models/combos fast and easy. Note: because the user can change the model and system message outside the preset, if they are to do that, it will invalidate the preset(it won't apply) and will require the user to edit/save the preset again with the current desired model/system message for it to work.
            *   **Keep Screen On** {{ic_backlight}} : When active it overrides the system settings and keeps the screen on.
            *   **Biometric Check** {{ic_fingerprint}} : Toggles fingerprint biometric security on or off. If on, the app will not open without a successful fingerprint reading by the system. 
            *   **Notification** {{ic_notinew}} : Receive notifications when the app is backgrounded and you receive a response.
            *   **Auto-disable Web Search**: Prevents the web search from auto turning off after each use.
            *   **LAN Button** {{ic_lan}} : Opens the LAN dialog where you can choose your LAN model provider and enter its endpoint.
            *   **Prompt Library Button** {{ic_prompts}}: Opens the Prompt Library Screen
            *   **Max Tokens Button** {{ic_ruler}} : Opens a dialog to set your Max Tokens value. Max Tokens limit the length of the AI's response. A higher number allows longer replies but may increase costs. Default is 12000.
            *   **API Key Button** {{ic_key}} : Opens a dialog to enter your OpenRouter API key.
             *  **Chat Memory : Allows the user to control context by limiting the amount of past messages sent in chat.
            *   **Brave Search API Key Button** {{ic_key}} : Opens a dialog to enter your Brave Search API key. The option for the tool will appear when long-pressing tools button. Allows for local or cloud models to have Brave search results injected into context.
            *   **OpenRouter Transforms** : To help with prompts that exceed the maximum context size of a model, OpenRouter supports a custom parameter called transforms. https://openrouter.ai/docs/guides/features/message-transforms
            *   **Extended Top Bar** : Allows the reasoning, web search, streaming, tools, presets, and setting buttons to be on chat screen all the time below the top bar for convenient access. Also includes a Home button to bring to Android Home screen.
            *   **Auto Back after receiving a Share** : If on, when you share text to a share target of the app, it will automatically take you back to the app you shared it from.
            *   **Copy Button on Notification** : If on it will replace the standard button with a Copy button on the notification which will copy the last AI response and dismiss the notification.
            
            ---

            ## 📂 App Screens

            ### Model Selection
            *   **Access**: Tap the **model name** at the top of the chat screen.
            *   **"Your Models" Screen**: Shows your list of available models. The default is **OpenRouter: Free**. Tap a model in the list to change the model for the current chat.
            *   **Vision**: If the model supports image upload the icon to the left of the name will show an image icon.
            *   **Image Generation**: If the model supports it the icon to the left of the name will show a palette icon.
            *   **Add a Model**: Use the floating action button to add a custom model.
            *   **Manage**: Tap the edit button on a model to **Edit** or **Delete** it.
            *   **Search**: Tap to search through the models.
            *   **Discover Models**: Tap the **cloud icon** to go to the **"OpenRouter Models"** screen. Tap the **LAN icon** to open a screen that will display your locally served available models.

            ### "OpenRouter Models" Screen
            *   **Sort**: Toggle between **Alphabetical** and **Newest**.
            *   **Search**: Tap to search through the models.
            *   **Add to Your List**: Tap any model to add it to your "Your Model" screen.
            *   **View Model Info**: Tap the icon on the right of the model to open its info page on the OpenRouter website.
            *   **Refresh**: Tap the **refresh icon** to get the latest list of models from OpenRouter.
            
            ### "LAN Models" Screen (Ollama, LM Studio, llama.cpp, MLX LM, and Hermes Agent served models)
            *   **Add to Your List**: Tap any model to add it to your "Your Models" screen.
            *   **Refresh**: The list will refresh when opened.
            
            ### "History" Screen
            *   **Open**: Tap **History** {{ic_schats}} in the top bar (or the history drawer when embedded in chat).
            *   **Import/Export**: **Settings → Data & Privacy**.
            *   **Overflow (⋮)**: Bottom sheet with **Delete** (red), **Rename**, **Pin**.
            *   **Search**: Search field in the History bottom bar (with Settings and New chat).
            *   **Autosave**: Conversations save automatically; rename via overflow.
            *   **Images**: Attached or generated images may not persist when you reopen a saved conversation.

            ### "System Message" Screen
            *   **Defaults**: Comes with "Default", "Spelling and Grammar corrector", and "Summarizer".
            *   **Add New**: Use the floating action button to create your own.
            *   **Hold and Drag** to reorder.
            *   **Manage**: Tap the edit button on a message to **Edit** or **Delete** it.
            *   **Import/Export**: Use the menu bar icons to manage your System Messages.
            
             ### "Prompt Library" Screen
            *  **Access**: Long-press the Presets button.
            *  Stores a library of prompts you may frequently use.  
            *  **Add New**: Use the floating action button to create a new one.
            *  **Hold and Drag** to reorder.
            *  **Tap** to copy to the clipboard.
            *  **Manage**: Tap the edit button on a message to **Edit** or **Delete** it.
            *  **Import/Export**: Use the menu bar icons to manage your Prompt Library.

            ---
            
            ## Image Generation Models
            
             *   Image generation models are denoted by a palette icon at the left of the model list item. The palette icon appears at the top of the main chat when you have a image generation model selected.
             *   Images are downloaded to your Downloads folder.
             *   Tap on the generated image in the chat to open it in your default image viewer.
             *   The generated images are not stored when conversations are saved to History.
             *   The generated images are not passed back in the chat. If you want the model to edit one you need to attach it manually.
            
            ---
            
            ## Audio Transcription Models
            
             *   Support for audio transcription models for OpenRouter and oMLX(Other LAN model servers don't support it as far as I know.)
             *   Click the mic icon on the main chat screen to load your audio file and then click send button.
             *   Only returns the transcript. These models do not chat.
             *   For a transcription model on oMLX, toggle the Transcription toggle in the edit model screen for it to be allowed to attach an audio file.
             *   Different models may support a different set of audio file formats.
             *   Non-streaming only. No tools are available for use.
             *   You can use one of these models as an alternate STT by long-pressing the microphone icon in the chat box. Set your STT model in Settings.
             *   If your have watermark transcription setting on, press and hold the watermark in the main chat screen while you talk. When you lift your finger, your speech is transcribed, copied to the clipboard and entered into the prompt box.
             *   If you have a transcription model set, make a preset entitled "Transcription"(case insensitive) and have Grokion set as your system's digital assistant(And do not have a preset entitled "Digital Assistant"), when you call the app using it as the assistant it will act as speech to text transcriber. It will automatically put the transcribed text on your clipboard and quit.
            ---
            
            ## Advanced Reasoning Settings
            
             *   You can enable it and make additional reasoing settings here. Long-press on reasoning button to make its screen appear.
             *   Enable reasoning traces in response, max reasoning tokens, and effort can be set here.
             *   Reasoning traces are not passed in the chat(they display only). Nor are they included in the whole chat pdf(but they will be in a singular response pdf). They are not saved in History.
             *   When enabled the Reasoning button will have a bright orange outline.
             
            ---

            ## ℹ️ Other Info

            *   An internet connection is required.
            *   If you want the app to work well while backgrounded, turn off battery optimization and allow background data usage in the system's settings for the app; Android Settings->Apps->Grokion->App battery usage->Allow Background usage->Unrestricted and Android Settings->Apps->Grokion->Mobile data usage->enable background data
            *   Review OpenRouter's and its model providers' pricing, privacy, and logging policies on their websites.
            *   Grokion does not have trackers, analytics, nor ads.
            *   This app is intended for use by person 18 and over.
            *   Chat history is stored on device only, encrypted at rest with SQLCipher (passphrase wrapped by Android Keystore). API keys are Keystore-encrypted. Cloud backup excludes prefs and the chat DB.
            *   The app is licensed under the Apache License 2.0.
            *   This app is not affiliated with OpenRouter.ai.
            *   Pasting in to the prompt box strips any unnecessary rich text formatting automatically.
            *   Costs are incurred with using OpenRouter models. Familiarize yourself with model costs at [https://openrouter.ai/models/](https://openrouter.ai/models/)
            *   Markdown content is well-supported in AI response chat messages.
            *   If you want to keep your chat history and/or System Messages, export them before you uninstall the app, otherwise they will be gone for good.
            *   Imports are programmed to not overwrite: System Messages skip duplicates by title, while History imports add new entries even when titles match, leaving all existing items intact.
            *   This open-source app is provided 'as-is' without any warranty, express or implied. Use at your own discretion.
            *   OpenRouter allows Presets which allow you to manage your LLM configurations—models, provider routing, and other features. You can use Presets in Grokion by just manually adding them in your model list. [https://openrouter.ai/docs/features/presets/](https://openrouter.ai/docs/features/presets/)
            *   The app is a target for multiple text shares: "Prompt"(set the prompt to the shared text), "System Message Chooser"(set the prompt to the shared text and sets the System Message as chosen in the popup), "Auto Send"(Auto sends the prompt to current model with current settings), and "Presets"(Allows the user to apply a chosen preset and options for the shared text.) You can also press the volume up or down buttons on this screen to stop/send the recording for transcription.
            *   Ollama, LM Studio, llama.cpp, MLX LM, and Hermes Agent endpoint default is plain http, therefore the chat is passed via unencrypted text on your LAN. Unless you have an https endpoint for them.
            *   For HTTPS LAN servers with a self-signed certificate, enable **Trust self-signed LAN TLS** in Settings (off by default). HTTP LAN URLs are limited to private/loopback/`.local` hosts.
            *   Ollama, LM Studio, llama.cpp, MLX LM, and Hermes Agent function is nascent and might not support all capabilities at this time. Furthermore you must set them to be served properly on your LAN.
            *   Notifications: With the bell enabled, you only get **"Your answer is ready."** when a response finishes while the app is backgrounded. Nothing posts while you're in the app. Returning to the app dismisses that notification.
            *   Notifications Buttons: "Dismiss" closes the notification. "Open" will bring Grokion to the foreground. "Speak" will speak aloud the last AI response. You stop the audio here too by pressing stop, dismissing, or swiping the notification away. This is separate from the main app's text-to-speak function.
            *   If you make a preset titled "Digital Assistant"(case-insensitive), and have Grokion as your system digital assistant in your Android settings(Settings->Apps->Default apps), this preset will be applied for when you use it as the system digital assistant.
            *   **Variable Substitution**: Use `{{oxdate}}` (yyyy-MM-dd), `{{oxtime}}` (HH:mm:ss), `{{oxdatetime}}` (ISO), or `{{oxhdt}}` (human-readable) in prompts/system messages. Auto-replaces with current date/time on send.
            *   Grokion changelogs: <br>[https://github.com/Warexpor/oxproxion/releases](https://github.com/Warexpor/oxproxion/releases) (also see `CHANGELOG.md` in the repo). Upstream oxproxion releases: <br>[https://github.com/stardomains3/oxproxion/releases](https://github.com/stardomains3/oxproxion/releases)
            *   If tools are not working, you may not have selected the correct folder. The app requires the **Download/grokion** folder for tools to work (legacy **Download/oxproxion** is still readable). [Click here to re-select it.](action://reselect-folder)
            *   Destructive file tools (delete/move/etc.) stay off unless you enable **Allow destructive file tools** in Settings.

            
            ## Hermes Agent Info:
            
            Use Hermes Agent v0.4.0 (v2026.3.23) or newer on your computer
            Enable the API server
            Add to `~/.hermes/.env`
            
             Environment Variables
             | Variable | Default | Description |
             |----------|---------|-------------|
             | `API_SERVER_ENABLED` | `false` | Enable the API server |
             | `API_SERVER_PORT` | `8642` | HTTP server port |
             | `API_SERVER_HOST` | `127.0.0.1` | Bind address (localhost only by default) |
             | `API_SERVER_KEY` | _(none)_ | Bearer token for auth |
             
             You would need to set API_SERVER_HOST to 0.0.0.0  and set a API_SERVER_KEY to be able to use it with Grokion on your LAN.
             Then enter the ip endpoint with port and API_SERVER_KEY you chose for the LAN setting in Grokion.
             The model will populate as "hermes-agent" in the LAN models list. Add it to your list and start chatting with the Hermes Agent on your computer.
             
             Use at own risk. See docs:
             
             https://github.com/NousResearch/hermes-agent/blob/main/website/docs/user-guide/features/api-server.md
             
             Will not act like Telegram, etc in that it can't message you whenever. It is just for back and forth chat: "List all the files on my MacBook's Desktop", "Check out example.com and tell me what it says" etc.. 
             Also it doesn't accept image input, nor tools from Grokion(Hermes Agent uses its own tools on the computer you have it on.)
             
             Check out Hermes Agent docs for all it can do:
             
             https://hermes-agent.nousresearch.com/
            
            ## What You Can Do With Grokion

            Grokion puts the power of multiple AI models at your fingertips. With custom system messages, you can tailor your experience for virtually any task. Here are some popular use cases:

            ### Language Translation
            Seamlessly translate conversations by setting a system message like:  
            "You are a professional translator. Translate all responses to [target language] while maintaining the original meaning and tone."

            ### Content Summarization
            Get concise overviews of lengthy articles with:  
            "You are a professional summarizer. Provide a clear, 3-paragraph summary of the following text, highlighting key points, evidence, and conclusions."

            ### Image Analysis & Learning
            Upload images and engage with them using vision models to get detailed descriptions, identify objects, explain concepts in diagrams, or learn from visual content.

            ### Code Development & Debugging
            Receive expert assistance with programming tasks across multiple languages. Ask for explanations, bug fixes, or new feature implementations with context-aware help.

            ### Personalized Companionship
            Create meaningful interactions with a virtual companion using:  
            "You are my supportive companion who shows genuine interest in my life, and provides thoughtful, caring responses."

            ### Educational Tutoring
            Get personalized learning experiences across subjects with:  
            "You are a patient tutor who explains complex concepts in simple terms with relevant examples, checks for understanding, and adapts to my learning pace."

            ### Creative Content Generation
            Craft stories, poems, scripts, or marketing copy with specific styles, tones, and requirements by setting appropriate system instructions.

            ### Research & Analysis
            Conduct deeper investigations on topics with assistance in finding information, analyzing data, and synthesizing findings into coherent insights.

            ### Language Practice
            Improve your language skills through conversation with a patient partner who corrects mistakes gently and explains grammar rules contextually. "You are a friendly language tutor who helps me practice [target language]. Correct my mistakes gently, explain grammar rules in simple terms, and respond using vocabulary appropriate for an intermediate learner. Keep the conversation natural and engaging."

            ### Professional Development
            Get tailored career advice, resume feedback, interview preparation, and industry-specific guidance to advance your professional journey. "You are a career coach who provides actionable advice on resume improvement, interview preparation, and professional growth. Be specific, constructive, and tailored to my industry and experience level."
                
            ### Share Chats
            Use the PDF button and share you chats with family, friends and co-workers.
            
            ---
            
            *   **Third-Party Licenses**: 
            
            [Tap here to view](oxproxion://licenses)
            
            highlight.js v11.10.0 (BSD 3-Clause License)

            Copyright (c) 2006, Ivan Sagalaev.
            All rights reserved.
            
            BSD 3-Clause License

            Copyright (c) 2006, Ivan Sagalaev.
            All rights reserved.

            Redistribution and use in source and binary forms, with or without
            modification, are permitted provided that the following conditions are met:

            * Redistributions of source code must retain the above copyright notice, this
              list of conditions and the following disclaimer.

            * Redistributions in binary form must reproduce the above copyright notice,
              this list of conditions and the following disclaimer in the documentation
              and/or other materials provided with the distribution.

            * Neither the name of the copyright holder nor the names of its
              contributors may be used to endorse or promote products derived from
              this software without specific prior written permission.

            THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
            AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
            IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
            DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
            FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
            DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
            SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
            CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
            OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
            OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
            
            KaTeX: v0.16.33 (MIT License)

            The MIT License (MIT)

            Copyright (c) 2013-2020 Khan Academy and other contributors

            Permission is hereby granted, free of charge, to any person obtaining a copy
            of this software and associated documentation files (the "Software"), to deal
            in the Software without restriction, including without limitation the rights
            to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
            copies of the Software, and to permit persons to whom the Software is
            furnished to do so, subject to the following conditions:

            The above copyright notice and this permission notice shall be included in all
            copies or substantial portions of the Software.

            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
            IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
            FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
            AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
            LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
            OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
            SOFTWARE.
            
            
            
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