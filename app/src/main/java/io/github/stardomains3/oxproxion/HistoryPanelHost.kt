package io.github.stardomains3.oxproxion

interface HistoryPanelHost {
    fun closeHistoryPanel(animated: Boolean = true)
    fun startNewChatFromHistory()
}
