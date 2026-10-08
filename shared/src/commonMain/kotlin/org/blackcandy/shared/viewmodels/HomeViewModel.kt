package org.blackcandy.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.blackcandy.shared.data.AlbumRepository
import org.blackcandy.shared.models.Album
import org.blackcandy.shared.models.AlertMessage
import org.blackcandy.shared.utils.TaskResult

data class HomeUiState(
    val recentlyPlayedAlbums: List<Album> = emptyList(),
    val recentlyAddedAlbums: List<Album> = emptyList(),
    val alertMessage: AlertMessage? = null,
)

class HomeViewModel(
    private val albumRepository: AlbumRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            when (val result = albumRepository.getRecentlyPlayedAlbums()) {
                is TaskResult.Success -> {
                    _uiState.update { it.copy(recentlyPlayedAlbums = result.data) }
                }

                is TaskResult.Failure -> {
                    showLoadFailure(result.message)
                }
            }

            when (val result = albumRepository.getRecentlyAddedAlbums()) {
                is TaskResult.Success -> {
                    _uiState.update { it.copy(recentlyAddedAlbums = result.data) }
                }

                is TaskResult.Failure -> {
                    showLoadFailure(result.message)
                }
            }
        }
    }

    fun alertMessageShown() {
        _uiState.update { it.copy(alertMessage = null) }
    }

    fun alertActionPerformed(action: AlertMessage.Action) {
        when (action) {
            AlertMessage.Action.RETRY -> load()
        }
    }

    private fun showLoadFailure(message: String?) {
        _uiState.update { it.copy(alertMessage = AlertMessage.String(message, AlertMessage.Action.RETRY)) }
    }
}
