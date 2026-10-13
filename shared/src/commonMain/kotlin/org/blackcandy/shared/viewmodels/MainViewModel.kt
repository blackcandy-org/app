package org.blackcandy.shared.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.runBlocking
import org.blackcandy.shared.data.UserRepository

class MainViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    val currentUserFlow = userRepository.getCurrentUserFlow()

    val currentUser =
        runBlocking {
            userRepository.getCurrentUser()
        }
}
