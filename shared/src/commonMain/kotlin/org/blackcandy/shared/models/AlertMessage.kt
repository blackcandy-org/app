package org.blackcandy.shared.models

sealed class AlertMessage {
    abstract val action: Action?

    enum class DefinedMessages {
        INVALID_SERVER_ADDRESS,
        UNSUPPORTED_SERVER,
        UNSUPPORTED_APP,
        ADDED_TO_PLAYLIST,
    }

    enum class Action {
        RETRY,
    }

    data class String(
        val value: kotlin.String?,
        override val action: Action? = null,
    ) : AlertMessage()

    data class LocalizedString(
        val value: DefinedMessages,
        override val action: Action? = null,
    ) : AlertMessage()
}
