package org.blackcandy.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.blackcandy.shared.api.Paged
import org.blackcandy.shared.data.CurrentPlaylistRepository
import org.blackcandy.shared.data.SongRepository
import org.blackcandy.shared.media.MusicServiceController
import org.blackcandy.shared.models.AlertMessage
import org.blackcandy.shared.models.Song
import org.blackcandy.shared.utils.TaskResult

data class SongsUiState(
    val loadedPages: Paged<Song>? = null,
    val isLoading: Boolean = false,
    val alertMessage: AlertMessage? = null,
) {
    val songs: List<Song> get() = loadedPages?.items ?: emptyList()
}

class SongsViewModel(
    private val songRepository: SongRepository,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val musicServiceController: MusicServiceController,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SongsUiState())

    val uiState = _uiState.asStateFlow()

    fun loadFirstPage() {
        load { songRepository.getSongs() }
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

    fun playNow(songId: Long) {
        viewModelScope.launch {
            val index = musicServiceController.getSongIndex(songId)

            if (index != -1) {
                musicServiceController.playOn(index)
                return@launch
            }

            val currentSong = musicServiceController.musicState.value.currentSong ?: return@launch

            when (val result = currentPlaylistRepository.addSongToNext(songId, currentSong.id)) {
                is TaskResult.Success -> {
                    val songIndex = musicServiceController.addSongToNext(result.data)
                    musicServiceController.playOn(songIndex)

                    _uiState.update {
                        it.copy(
                            alertMessage = AlertMessage.LocalizedString(AlertMessage.DefinedMessages.ADDED_TO_PLAYLIST),
                        )
                    }
                }

                is TaskResult.Failure -> {
                    _uiState.update { it.copy(alertMessage = AlertMessage.String(result.message)) }
                }
            }
        }
    }

    fun playNext(songId: Long) {
        viewModelScope.launch {
            val currentSong = musicServiceController.musicState.value.currentSong ?: return@launch

            when (val result = currentPlaylistRepository.addSongToNext(songId, currentSong.id)) {
                is TaskResult.Success -> {
                    musicServiceController.addSongToNext(result.data)

                    _uiState.update {
                        it.copy(
                            alertMessage = AlertMessage.LocalizedString(AlertMessage.DefinedMessages.ADDED_TO_PLAYLIST),
                        )
                    }
                }

                is TaskResult.Failure -> {
                    _uiState.update { it.copy(alertMessage = AlertMessage.String(result.message)) }
                }
            }
        }
    }

    fun playLast(songId: Long) {
        viewModelScope.launch {
            when (val result = currentPlaylistRepository.addSongToLast(songId)) {
                is TaskResult.Success -> {
                    musicServiceController.addSongToLast(result.data)

                    _uiState.update {
                        it.copy(
                            alertMessage = AlertMessage.LocalizedString(AlertMessage.DefinedMessages.ADDED_TO_PLAYLIST),
                        )
                    }
                }

                is TaskResult.Failure -> {
                    _uiState.update { it.copy(alertMessage = AlertMessage.String(result.message)) }
                }
            }
        }
    }

    private fun retry() {
        if (_uiState.value.loadedPages == null) loadFirstPage() else loadNextPage()
    }

    private fun load(request: suspend () -> TaskResult<Paged<Song>>) {
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
