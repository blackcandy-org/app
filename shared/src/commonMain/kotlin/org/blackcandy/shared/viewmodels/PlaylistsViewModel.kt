package org.blackcandy.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.blackcandy.shared.api.Paged
import org.blackcandy.shared.data.PlaylistRepository
import org.blackcandy.shared.models.AlertMessage
import org.blackcandy.shared.models.Playlist
import org.blackcandy.shared.utils.TaskResult

data class PlaylistsUiState(
    val loadedPages: Paged<Playlist>? = null,
    val isLoading: Boolean = false,
    val alertMessage: AlertMessage? = null,
) {
    val playlists: List<Playlist> get() = loadedPages?.items ?: emptyList()
}

class PlaylistsViewModel(
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaylistsUiState())

    val uiState = _uiState.asStateFlow()

    fun loadFirstPage() {
        load { playlistRepository.getPlaylists() }
    }

    fun loadNextPage() {
        val pages = _uiState.value.loadedPages ?: return
        if (!pages.hasNextPage) return

        load { pages.next() }
    }

    fun alertMessageShown() {
        _uiState.update { it.copy(alertMessage = null) }
    }

    fun alertActionPerformed(action: AlertMessage.Action) {
        when (action) {
            AlertMessage.Action.RETRY -> retry()
        }
    }

    private fun retry() {
        if (_uiState.value.loadedPages == null) loadFirstPage() else loadNextPage()
    }

    private fun load(request: suspend () -> TaskResult<Paged<Playlist>>) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = request()) {
                is TaskResult.Success -> {
                    _uiState.update { it.copy(loadedPages = result.data, isLoading = false) }
                }

                is TaskResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            alertMessage = AlertMessage.String(result.message, AlertMessage.Action.RETRY),
                        )
                    }
                }
            }
        }
    }
}
