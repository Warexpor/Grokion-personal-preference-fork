package io.github.stardomains3.oxproxion

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readLine

/**
 * Shared SSE / NDJSON payload reader for chat streaming.
 * Extracted from ChatViewModel — behavior preserved.
 */
object SseJsonReader {
    /**
     * Reads SSE (`data:` lines, blank-line delimited) with NDJSON fallback.
     * Does not stop early on [DONE] (some providers send trailing chunks after it).
     */
    suspend fun forEachJsonPayload(
        channel: ByteReadChannel,
        onPayload: suspend (String) -> Unit
    ) {
        val dataLines = mutableListOf<String>()
        suspend fun flushData() {
            if (dataLines.isEmpty()) return
            val payload = dataLines.joinToString("\n").trim()
            dataLines.clear()
            if (payload.isNotEmpty() &&
                payload != "[DONE]" &&
                !payload.equals("DONE", ignoreCase = true)
            ) {
                onPayload(payload)
            }
        }
        try {
            while (!channel.isClosedForRead) {
                val line = channel.readLine() ?: break
                when {
                    line.isEmpty() -> flushData()
                    line.startsWith(":") -> Unit // comment / keepalive
                    line.startsWith("data:") -> {
                        val value = when {
                            line.startsWith("data: ") -> line.substring(6)
                            line.startsWith("data:\t") -> line.substring(6)
                            else -> line.substring(5).trimStart()
                        }
                        dataLines.add(value)
                    }
                    line.startsWith("event:") || line.startsWith("id:") || line.startsWith("retry:") -> Unit
                    line.trimStart().startsWith("{") -> {
                        flushData()
                        onPayload(line.trim())
                    }
                    dataLines.isNotEmpty() -> dataLines.add(line)
                }
            }
            flushData()
        } catch (_: Exception) {
            flushData()
        }
    }
}
