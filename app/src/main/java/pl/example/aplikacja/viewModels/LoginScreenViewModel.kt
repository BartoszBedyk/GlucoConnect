package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.UserCredentials
import pl.example.networkmodule.saveToken


class LoginScreenViewModel(private val apiProvider: ApiProvider): ViewModel() {
    private val authenticationApi = apiProvider.authenticationApi

    private val _healthy = MutableStateFlow<Boolean?>(false)
    val healthy: MutableStateFlow<Boolean?> = _healthy

    init {
        isApiAvilible(apiProvider.innerContext)
    }
    suspend fun login(login: String, password: String, context: Context): String? {
        return try {
            val userCredentials = UserCredentials(login, password)
            val token = authenticationApi.login(userCredentials)
            if (token != null) {
                saveToken(context, token)
                Log.d("LoginScreen", "Login successful, token: $token")
                token
            } else {
                Log.e("LoginScreen", "Login failed: invalid credentials")
                null
            }
        } catch (e: Exception) {
            if (!isNetworkAvailable(context)) {
                Log.e("LoginScreen", "Login failed: No internet connection")
            } else {
                Log.e("LoginScreen", "Login failed: ${e.message}")
            }
            null
        }
    }

     fun isApiAvilible(context: Context) {
        viewModelScope.launch {
            try {
                if (authenticationApi.isApiAvlible() == true && isNetworkAvailable(context)) {
                    _healthy.value = true
                } else {
                    _healthy.value = false
                }
            } catch (e: Exception) {
                _healthy.value = false
            }
        }
    }

    suspend fun refreshToken(context: Context): String? {
        return try {
            val currentToken = getToken(context)
            if (currentToken == null) {
                Log.e("LoginScreen", "Refresh failed: No token available")
                return null
            }

            val newToken = withContext(Dispatchers.IO) {
                authenticationApi.refreshToken(currentToken)
            }

            if (newToken != null) {
                saveToken(context, newToken)
                Log.d("LoginScreen", "Token refreshed successfully: $newToken")
                newToken
            } else {
                Log.e("LoginScreen", "Refresh failed: Unable to get new token")
                null
            }
        } catch (e: Exception) {
            if (!isNetworkAvailable(context)) {
                Log.e("LoginScreen", "Refresh failed: No internet connection")
            } else {
                Log.e("LoginScreen", "Refresh failed: ${e.message}")
            }
            null
        }
    }


}