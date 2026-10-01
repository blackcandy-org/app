package org.blackcandy.android.utils

import android.app.Activity
import android.view.View
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.google.android.material.snackbar.Snackbar
import org.blackcandy.android.R
import org.blackcandy.shared.models.AlertMessage

class SnackbarUtil {
    companion object {
        @Composable
        fun ShowSnackbar(
            message: AlertMessage,
            state: SnackbarHostState,
            onAction: ((AlertMessage.Action) -> Unit)? = null,
            onShown: () -> Unit,
        ) {
            val snackbarText =
                when (message) {
                    is AlertMessage.String -> message.value
                    is AlertMessage.LocalizedString -> stringResource(getLocalizedString(message.value))
                } ?: return

            val action = message.action
            val actionLabel = if (action != null && onAction != null) stringResource(getActionText(action)) else null

            LaunchedEffect(state, message) {
                // A snackbar with an action doesn't time out, so it also gets a close button.
                val result =
                    state.showSnackbar(
                        snackbarText,
                        actionLabel = actionLabel,
                        withDismissAction = actionLabel != null,
                    )

                onShown()

                if (result == SnackbarResult.ActionPerformed && action != null) {
                    onAction?.invoke(action)
                }
            }
        }

        fun showSnackbar(
            activity: Activity,
            message: AlertMessage,
            onShown: () -> Unit,
        ) {
            val rootView = activity.findViewById<View>(R.id.main_layout)

            val snackbarText =
                when (message) {
                    is AlertMessage.String -> message.value
                    is AlertMessage.LocalizedString -> rootView.context.getString(getLocalizedString(message.value))
                } ?: return

            Snackbar
                .make(rootView, snackbarText, Snackbar.LENGTH_SHORT)
                .addCallback(
                    object : Snackbar.Callback() {
                        override fun onShown(sb: Snackbar?) {
                            super.onShown(sb)
                            onShown()
                        }
                    },
                ).show()
        }

        fun getLocalizedString(definedMessage: AlertMessage.DefinedMessages): Int =
            when (definedMessage) {
                AlertMessage.DefinedMessages.UNSUPPORTED_SERVER -> R.string.unsupported_server
                AlertMessage.DefinedMessages.INVALID_SERVER_ADDRESS -> R.string.invalid_server_address
                AlertMessage.DefinedMessages.UNSUPPORTED_APP -> R.string.unsupported_app
                AlertMessage.DefinedMessages.ADDED_TO_PLAYLIST -> R.string.added_to_playlist
            }

        fun getActionText(action: AlertMessage.Action): Int =
            when (action) {
                AlertMessage.Action.RETRY -> R.string.retry
            }
    }
}
