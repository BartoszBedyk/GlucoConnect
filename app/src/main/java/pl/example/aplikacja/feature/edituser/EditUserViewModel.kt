package pl.example.aplikacja.feature.edituser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.JwtHelper
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiMethods.UserApiInterface
import pl.example.networkmodule.requestData.UpdateUserNullForm
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(private val userApi: UserApiInterface, jwtHelper: JwtHelper) :
    ViewModel() {

    private val userId: String = jwtHelper.getUserId()

    private val _userData = MutableStateFlow<UserResult?>(null)
    val userData: MutableStateFlow<UserResult?> = _userData

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _userData.value = userApi.getUserById(id = userId)
        }
    }

    suspend fun editUserData(editData: UpdateUserNullForm): Boolean {
        Log.d("EditUserViewModel", "editUserData: $editData")
        return userApi.updateUserNulls(editData)
    }
}
