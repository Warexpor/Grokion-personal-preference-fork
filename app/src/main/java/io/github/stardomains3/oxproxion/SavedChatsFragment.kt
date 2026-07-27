package io.github.stardomains3.oxproxion

import io.github.stardomains3.oxproxion.Motion.withGrokStackAnimations

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SavedChatsFragment : Fragment() {

    companion object {
        private const val ARG_EMBEDDED = "embedded"

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
            onOverflowClick = { session, _ -> showOptionsSheet(session) }
        )

        recyclerView.adapter = savedChatsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // R→L swipe closes history (Grok drawer dismiss)
        val closeDetector = (parentFragment as? ChatFragment)?.historyCloseSwipeDetector
        if (closeDetector != null) {
            view.setOnTouchListener { _, event ->
                closeDetector.onTouchEvent(event)
                false
            }
            recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    closeDetector.onTouchEvent(e)
                    return false
                }
            })
        }

        savedChatsViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            allSessions = sessions ?: emptyList()
            filterSessions(searchView.query?.toString().orEmpty())
        }
    }

    private fun openSettings() {
        if (isEmbedded) {
            (parentFragment as? HistoryPanelHost)?.openSettingsFromHistory()
        } else {
            parentFragmentManager.beginTransaction()
                .withGrokStackAnimations()
                .hide(this)
                .add(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
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

            val items = buildList {
                if (pinned.isNotEmpty()) {
                    add(HistoryListItem.Header(getString(R.string.grok_history_pinned_title)))
                    pinned.forEach { add(HistoryListItem.Session(it, pinned = true)) }
                }
                if (rest.isNotEmpty()) {
                    // Grok parity: single "Conversations" section (not day buckets)
                    add(HistoryListItem.Header(getString(R.string.grok_history_conversations_title)))
                    rest.forEach { add(HistoryListItem.Session(it, pinned = false)) }
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

    private fun showOptionsSheet(session: ChatSession) {
        val dialog = BottomSheetDialog(requireContext(), R.style.ThemeOverlay_Grokion_BottomSheet)
        val sheet = layoutInflater.inflate(R.layout.bottom_sheet_history_item, null)
        dialog.setContentView(sheet)

        val pinned = prefs.isSessionPinned(session.id)
        val pinButton = sheet.findViewById<MaterialButton>(R.id.menu_pin)
        pinButton.text = getString(if (pinned) R.string.grok_history_unpin else R.string.grok_history_pin)

        sheet.findViewById<View>(R.id.menu_delete).setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmationDialog(session)
        }
        sheet.findViewById<View>(R.id.menu_edit).setOnClickListener {
            dialog.dismiss()
            showRenameDialog(session)
        }
        pinButton.setOnClickListener {
            dialog.dismiss()
            prefs.setSessionPinned(session.id, !pinned)
            filterSessions(searchView.query?.toString().orEmpty())
        }

        dialog.show()
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
