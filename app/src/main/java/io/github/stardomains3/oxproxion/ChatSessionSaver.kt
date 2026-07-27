package io.github.stardomains3.oxproxion

/**
 * Session persistence helpers — overwrite semantics live in [ChatDao.insertSessionAndMessages]
 * (delete messages for session, then insert).
 */
object ChatSessionSaver {
    suspend fun save(
        repository: ChatRepository,
        session: ChatSession,
        messages: List<ChatMessage>
    ) {
        repository.insertSessionAndMessages(session, messages)
    }
}
