package pl.example.aplikacja.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor() : ViewModel() {
    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun updateUsername(newUsername: String) {
        Log.d("LoginScreenViewModel", "Updating username to $newUsername")
        username = newUsername
    }

    fun updatePassword(newPassword: String) {
        password = newPassword

    }



}