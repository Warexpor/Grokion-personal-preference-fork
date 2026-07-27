package io.github.stardomains3.oxproxion

import java.net.URI

/**
 * LAN endpoint rules: HTTPS anywhere; HTTP only to loopback / private / link-local /
 * `.local` hosts (homelab). Blocks cleartext to public IP literals.
 */
object LanEndpointValidator {
    /** @return error message, or null if valid */
    fun validate(rawUrl: String): String? {
        val url = rawUrl.trim()
        if (url.isBlank()) return "Please enter a LAN endpoint URL"
        val uri = try {
            URI(url)
        } catch (_: Exception) {
            return "Invalid URL"
        }
        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") {
            return "URL must use http:// or https://"
        }
        val host = uri.host?.trim().orEmpty()
        if (host.isEmpty()) return "URL must include a host"
        if (scheme == "http" && !isPrivateOrLocalHost(host)) {
            return "HTTP LAN endpoints must use localhost, a private IP (10/8, 172.16–31, 192.168), link-local, or a .local name. Use HTTPS for other hosts."
        }
        return null
    }

    fun isPrivateOrLocalHost(host: String): Boolean {
        val h = host.trim().lowercase().removePrefix("[").removeSuffix("]")
        if (h == "localhost" || h == "127.0.0.1" || h == "::1" || h == "0:0:0:0:0:0:0:1") return true
        if (h.endsWith(".local")) return true
        val parts = h.split('.')
        if (parts.size == 4 && parts.all { it.toIntOrNull() in 0..255 }) {
            val a = parts[0].toInt()
            val b = parts[1].toInt()
            return a == 10 ||
                a == 127 ||
                (a == 172 && b in 16..31) ||
                (a == 192 && b == 168) ||
                (a == 169 && b == 254)
        }
        // Non-IP hostname (router DNS / mDNS without .local) — allow for homelab HTTP
        return !h.contains(':') && parts.size >= 1 && parts[0].any { it.isLetter() }
    }
}
