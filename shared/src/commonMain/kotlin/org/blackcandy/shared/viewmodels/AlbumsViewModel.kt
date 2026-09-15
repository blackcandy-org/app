package org.blackcandy.shared.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.blackcandy.shared.api.PageInfo
import org.blackcandy.shared.data.AlbumRepository
import org.blackcandy.shared.models.Album
import org.blackcandy.shared.models.AlertMessage
import org.blackcandy.shared.utils.TaskResult

data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val pageInfo: PageInfo? = null,
    val isLoading: Boolean = false,
    val alertMessage: AlertMessage? = null,
)

class AlbumsViewModel(
    private val albumRepository: AlbumRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlbumsUiState())

    private var lastRequest = 1 to false

    val uiState = _uiState.asStateFlow()

    fun loadFirstPage() {
        load(page = 1, append = false)
    }

    fun loadNextPage() {
        val nextPage = _uiState.value.pageInfo?.nextPage ?: return
        load(page = nextPage, append = true)
    }

    fun retry() {
        val (page, append) = lastRequest
        load(page = page, append = append)
    }

    fun alertMessageShown() {
        _uiState.update { it.copy(alertMessage = null) }
    }

    private fun load(
        page: Int,
        append: Boolean,
    ) {
        if (_uiState.value.isLoading) return

        lastRequest = page to append

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = albumRepository.getAlbums(page)) {
                is TaskResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            albums =
                                if (append) {
                                    state.albums + result.data.items
                                } else {
                                    result.data.items
                                },
                            pageInfo = result.data.pageInfo,
                            isLoading = false,
                        )
                    }
                }

                is TaskResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            alertMessage = AlertMessage.String(result.message),
                        )
                    }
                }
            }
        }
    }
}
