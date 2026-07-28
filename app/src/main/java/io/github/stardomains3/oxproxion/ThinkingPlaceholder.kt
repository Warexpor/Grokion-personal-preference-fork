package io.github.stardomains3.oxproxion

object ThinkingPlaceholder {
    const val TOKEN = "thinking..."
    private const val LEGACY = "working..."

    fun matches(text: String): Boolean = text == TOKEN || text == LEGACY
}
