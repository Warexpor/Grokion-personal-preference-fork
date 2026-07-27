package io.github.stardomains3.oxproxion

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatDaoOverwriteInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ChatDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.chatDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun overwriteReplacesMessagesWithoutDuplicating() = runBlocking {
        val sessionId = 1L
        val session = ChatSession(id = sessionId, title = "t", modelUsed = "m")

        dao.insertSessionAndMessages(
            session,
            listOf(
                ChatMessage(sessionId = sessionId, role = "user", content = "\"hi\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"hello\"")
            )
        )
        assertEquals(2, dao.getMessagesForSession(sessionId).size)

        dao.insertSessionAndMessages(
            session.copy(title = "t2"),
            listOf(
                ChatMessage(sessionId = sessionId, role = "user", content = "\"hi\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"hello\""),
                ChatMessage(sessionId = sessionId, role = "user", content = "\"again\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"ok\"")
            )
        )
        assertEquals(4, dao.getMessagesForSession(sessionId).size)

        dao.insertSessionAndMessages(
            session.copy(title = "t2"),
            listOf(
                ChatMessage(sessionId = sessionId, role = "user", content = "\"hi\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"hello\""),
                ChatMessage(sessionId = sessionId, role = "user", content = "\"again\""),
                ChatMessage(sessionId = sessionId, role = "assistant", content = "\"ok\"")
            )
        )
        assertEquals(4, dao.getMessagesForSession(sessionId).size)
    }
}
