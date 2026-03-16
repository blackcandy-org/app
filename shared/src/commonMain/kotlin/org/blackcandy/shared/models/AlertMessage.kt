package org.blackcandy.shared.models

sealed class AlertMessage {
    enum class DefinedMessages {
        INVALID_SERVER_ADDRESS,
        UNSUPPORTED_SERVER,
        UNSUPPORTED_APP,
        ADDED_TO_PLAYLIST,
    }

    data class String(
        val value: kotlin.String?,
    ) : AlertMessage()

    data class LocalizedString(
        val value: DefinedMessages,
    ) : AlertMessage()
}
