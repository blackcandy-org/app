package org.blackcandy.shared.api

object LinkHeader {
    private val LINK_PATTERN = """<([^>]*)>\s*;\s*rel\s*=\s*"?([^",;]+)"?""".toRegex()

    fun parse(value: String?): Map<String, String> {
        if (value.isNullOrBlank()) return emptyMap()

        return LINK_PATTERN
            .findAll(value)
            .associate { match ->
                match.groupValues[2].trim() to match.groupValues[1].trim()
            }
    }
}
