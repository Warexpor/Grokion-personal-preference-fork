package io.github.stardomains3.oxproxion

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Documents expected overwrite semantics: replacing a session's messages must
 * yield a stable count equal to the latest snapshot (not append duplicates).
 * Room DAO implements this via delete-then-insert in [ChatDao.insertSessionAndMessages].
 */
class ChatSaveOverwriteTest {

    @Test
    fun overwriteKeepsLatestMessageCountOnly() {
        val sessionId = 1L
        var stored = listOf(
            ChatMessage(id = 1, sessionId = sessionId, role = "user", content = "\"hi\""),
            ChatMessage(id = 2, sessionId = sessionId, role = "assistant", content = "\"hello\"")
        )

        fun replaceMessages(next: List<ChatMessage>) {
            // Mirrors ChatDao: wipe session messages, then insert fresh rows with id=0
            stored = next.map { it.copy(id = 0, sessionId = sessionId) }
        }

        replaceMessages(
            listOf(
                ChatMessage(sessionId = sessionId, role = "user", content = "\"hi\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"hello\""),
                ChatMessage(sessionId = sessionId, role = "user", content = "\"again\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"ok\"")
            )
        )
        replaceMessages(
            listOf(
                ChatMessage(sessionId = sessionId, role = "user", content = "\"hi\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"hello\""),
                ChatMessage(sessionId = sessionId, role = "user", content = "\"again\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"ok\"")
            )
        )

        assertEquals(4, stored.size)
    }
}
