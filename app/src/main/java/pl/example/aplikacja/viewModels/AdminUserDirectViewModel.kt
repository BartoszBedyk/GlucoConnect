package pl.example.aplikacja.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.toRestrictedUserTypeOrNull
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.RestrictedUserType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.UnitUpdate

class AdminUserDirectViewModel(apiProvider: ApiProvider, private val USER_ID: String) :
    ViewModel() {

    private val userApi = apiProvider.userApi

    private val _userData = MutableStateFlow<UserResult?>(null)
    val userData: MutableStateFlow<UserResult?> = _userData

    private val _userType = MutableStateFlow<RestrictedUserType?>(null)
    val userType: MutableStateFlow<RestrictedUserType?> = _userType

    init {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            try {
                _userData.value = userApi.getUserById(USER_ID)
                _userType.value = _userData.value?.type?.toRestrictedUserTypeOrNull()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    suspend fun blockUser(): Boolean {
        try {
            return userApi.blockUser(USER_ID)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

    suspend fun unblcokUser(): Boolean {
        try {
            return userApi.unblockUser(USER_ID)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }

    }

    suspend fun updateType(type: String): Boolean {
        try {
            return userApi.changeUserType(USER_ID, type)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

    suspend fun updateUnit(unitUpdate: UnitUpdate): Boolean {
        try {
            return userApi.unitUpdate(unitUpdate)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

    suspend fun deleteUser(): Boolean {
        try {
            Log.i("AdminUserDirectViewModel", "Deleting user with ID: $USER_ID")
            return userApi.deleteUser(USER_ID)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

    suspend fun resetPassword(newPassword: String): Boolean {
        try {
            return userApi.resetPassword(USER_ID, newPassword)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }
}
