package io.github.stardomains3.oxproxion

import io.github.stardomains3.oxproxion.Motion.withGrokStackAnimations

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class SavedChatsFragment : Fragment() {

    companion object {
        private const val ARG_EMBEDDED = "embedded"
        private const val DAY_MS = 24L * 60L * 60L * 1000L

        fun newEmbedded(): SavedChatsFragment = SavedChatsFragment().apply {
            arguments = Bundle().apply { putBoolean(ARG_EMBEDDED, true) }
        }
    }

    private val isEmbedded: Boolean
        get() = arguments?.getBoolean(ARG_EMBEDDED) == true

    private val viewModel: ChatViewModel by activityViewModels()
    private val savedChatsViewModel: SavedChatsViewModel by viewModels()
    private lateinit var savedChatsAdapter: SavedChatsAdapter
    private lateinit var searchView: SearchView
    private lateinit var historyEmptyView: TextView
    private lateinit var prefs: SharedPreferencesHelper
    private var allSessions: List<ChatSession> = emptyList()

    private val exportChatsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val json = savedChatsViewModel.getChatsAsJson()
                        requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.write(json.toByteArray())
                        }
                        AppToast.makeText(requireContext(), "Chats exported successfully", AppToast.LENGTH_SHORT).show()
                    } catch (_: Exception) {
                        AppToast.makeText(requireContext(), "Error exporting chats", AppToast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val importChatsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val json = requireContext().contentResolver.openInputStream(uri)?.use {
                            it.bufferedReader().readText()
                        }
                        if (json != null) {
                            savedChatsViewModel.importChatsFromJson(json) { importResult ->
                                when (importResult) {
                                    is ChatImportResult.Success ->
                                        AppToast.makeText(requireContext(), "Chats imported successfully", AppToast.LENGTH_SHORT).show()
                                    is ChatImportResult.Error ->
                                        AppToast.makeText(requireContext(), importResult.message, AppToast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            throw Exception("Failed to read file content.")
                        }
                    } catch (_: Exception) {
                        AppToast.makeText(requireContext(), "Import failed. Check file format.", AppToast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_saved_chats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SharedPreferencesHelper(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.savedChatsRecyclerView)
        historyEmptyView = view.findViewById(R.id.historyEmptyView)
        searchView = view.findViewById(R.id.historySearchView)

        val closeButton = view.findViewById<ImageButton>(R.id.historyCloseButton)
        val settingsButton = view.findViewById<ImageButton>(R.id.historySettingsButton)

        // Embedded: collapse drawer. Full-screen: pop back.
        closeButton.setOnClickListener {
            if (isEmbedded) {
                (parentFragment as? HistoryPanelHost)?.closeHistoryPanel()
            } else {
                parentFragmentManager.popBackStack()
            }
        }

        settingsButton.setOnClickListener { openSettings() }

        view.findViewById<ImageButton>(R.id.historyImportButton).setOnClickListener { importChats() }
        view.findViewById<ImageButton>(R.id.historyExportButton).setOnClickListener { exportChats() }

        view.findViewById<ImageButton>(R.id.historyNewChatButton).setOnClickListener {
            if (isEmbedded) {
                (parentFragment as? HistoryPanelHost)?.startNewChatFromHistory()
            } else {
                viewModel.startNewChat()
                parentFragmentManager.popBackStack()
            }
        }

        searchView.queryHint = getString(R.string.grok_history_search)
        // Quiet SearchView chrome
        searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)?.setBackgroundColor(Color.TRANSPARENT)
        var searchJob: Job? = null
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    filterSessions(newText ?: "")
                }
                return true
            }
        })

        savedChatsAdapter = SavedChatsAdapter(
            onClick = { session ->
                viewModel.loadChat(session.id)
                if (isEmbedded) {
                    (parentFragment as? HistoryPanelHost)?.closeHistoryPanel()
                } else {
                    parentFragmentManager.popBackStack()
                }
            },
            onOverflowClick = { session, anchor -> showOptionsDialog(session, anchor) }
        )

        recyclerView.adapter = savedChatsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        savedChatsViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            allSessions = sessions ?: emptyList()
            filterSessions(searchView.query?.toString().orEmpty())
        }
    }

    private fun openSettings() {
        if (isEmbedded) {
            (parentFragment as? HistoryPanelHost)?.closeHistoryPanel(animated = false)
        }
        val host = if (isEmbedded) {
            requireActivity().supportFragmentManager
        } else {
            parentFragmentManager
        }
        val chat = requireActivity().supportFragmentManager.fragments
            .firstOrNull { it is ChatFragment && it.isAdded }
        host.beginTransaction()
            .withGrokStackAnimations()
            .apply {
                if (chat != null) hide(chat)
                else if (!isEmbedded) hide(this@SavedChatsFragment)
            }
            .add(R.id.fragment_container, SettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun startOfDayMillis(now: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun bucketLabel(timestamp: Long, startToday: Long): String {
        return when {
            timestamp >= startToday -> getString(R.string.grok_history_today)
            timestamp >= startToday - DAY_MS -> getString(R.string.grok_history_yesterday)
            timestamp >= startToday - 7L * DAY_MS -> getString(R.string.grok_history_previous_7_days)
            else -> getString(R.string.grok_history_older)
        }
    }

    private fun filterSessions(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val filtered = if (query.isEmpty()) {
                allSessions
            } else {
                savedChatsViewModel.searchSessions(query)
            }
            val pinnedIds = prefs.getPinnedSessionIds()
            val pinned = filtered.filter { it.id in pinnedIds }
                .sortedByDescending { it.timestamp }
            val rest = filtered.filter { it.id !in pinnedIds }
                .sortedByDescending { it.timestamp }

            val startToday = startOfDayMillis()
            val items = buildList {
                if (pinned.isNotEmpty()) {
                    add(HistoryListItem.Header(getString(R.string.grok_history_pinned_title)))
                    pinned.forEach { add(HistoryListItem.Session(it, pinned = true)) }
                }
                var lastBucket: String? = null
                rest.forEach { session ->
                    val bucket = bucketLabel(session.timestamp, startToday)
                    if (bucket != lastBucket) {
                        add(HistoryListItem.Header(bucket))
                        lastBucket = bucket
                    }
                    add(HistoryListItem.Session(session, pinned = false))
                }
            }
            savedChatsAdapter.submitList(items)

            val empty = filtered.isEmpty()
            historyEmptyView.isVisible = empty
            view?.findViewById<RecyclerView>(R.id.savedChatsRecyclerView)?.isVisible = !empty
            historyEmptyView.text = if (query.isBlank()) {
                "${getString(R.string.grok_history_empty_title)}\n\n${getString(R.string.grok_history_empty_text)}"
            } else {
                "${getString(R.string.grok_history_search_empty_title)}\n\n${getString(R.string.grok_history_search_empty_text)}"
            }
        }
    }

    private fun exportChats() {
        if (allSessions.isEmpty()) {
            AppToast.makeText(requireContext(), "No chats to export.", AppToast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "openchat_backup.json")
        }
        exportChatsLauncher.launch(intent)
    }

    private fun importChats() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        importChatsLauncher.launch(intent)
    }

    private fun showOptionsDialog(session: ChatSession, anchorView: View) {
        val menuView = LayoutInflater.from(requireContext()).inflate(R.layout.saved_popup_layout, null)
        val popupWindow = PopupWindow(
            menuView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        popupWindow.isOutsideTouchable = true

        val rootView = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val dimView = View(requireContext()).apply {
            setBackgroundColor(Color.argb(153, 0, 0, 0))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        rootView.addView(dimView)
        popupWindow.setOnDismissListener { rootView.removeView(dimView) }

        val pinOption = menuView.findViewById<TextView>(R.id.menu_pin)
        val renameOption = menuView.findViewById<TextView>(R.id.menu_edit)
        val deleteOption = menuView.findViewById<TextView>(R.id.menu_delete)
        val pinned = prefs.isSessionPinned(session.id)
        pinOption.text = getString(if (pinned) R.string.grok_history_unpin else R.string.grok_history_pin)

        pinOption.setOnClickListener {
            popupWindow.dismiss()
            prefs.setSessionPinned(session.id, !pinned)
            filterSessions(searchView.query?.toString().orEmpty())
        }
        renameOption.setOnClickListener {
            popupWindow.dismiss()
            showRenameDialog(session)
        }
        deleteOption.setOnClickListener {
            popupWindow.dismiss()
            showDeleteConfirmationDialog(session)
        }

        menuView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        popupWindow.showAsDropDown(anchorView, anchorView.width - menuView.measuredWidth, -anchorView.height / 2)
    }

    private fun showRenameDialog(session: ChatSession) {
        val editText = EditText(requireContext()).apply {
            setText(session.title)
            setSelection(session.title.length)
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Rename")
            .setView(editText)
            .setPositiveButton("Rename") { _, _ ->
                val newTitle = editText.text.toString()
                if (newTitle.isNotBlank()) {
                    savedChatsViewModel.updateSessionTitle(session.id, newTitle)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(session: ChatSession) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.grok_history_delete_title)
            .setMessage(R.string.grok_history_delete_description)
            .setPositiveButton("Delete") { _, _ ->
                prefs.setSessionPinned(session.id, false)
                savedChatsViewModel.deleteSession(session.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
