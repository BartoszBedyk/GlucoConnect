package pl.example.aplikacja.feature.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.UserCredentials
import pl.example.networkmodule.saveToken
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(private val authenticationApi: AuthenticationApiInterface,
    apiProvider: ApiProvider): ViewModel() {

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


    var lastCheckedTime = 0L
    fun isApiAvilible(context: Context) {

        val now = System.currentTimeMillis()
        if (now - lastCheckedTime < 10_000) return
        lastCheckedTime = now

        viewModelScope.launch {
            try {
                _healthy.value =
                    authenticationApi.isApiAvlible() == true && isNetworkAvailable(context)
            } catch (e: Exception) {
                _healthy.value = false
            }
        }
    }


    suspend fun refreshToken(context: Context): String? = withContext(Dispatchers.IO) {
        val currentToken = getToken(context) ?: run {
            Log.e("Auth", "Refresh failed: No token available")
            return@withContext null
        }

        if (!isNetworkAvailable(context)) {
            Log.e("Auth", "Refresh failed: No internet connection")
            return@withContext null
        }

        return@withContext try {
            val newToken = authenticationApi.refreshToken(currentToken)
            if (newToken.isNullOrBlank()) {
                Log.e("Auth", "Refresh failed: Empty or null token from server")
                null
            } else {
                saveToken(context, newToken)
                Log.d("Auth", "Token refreshed successfully")
                newToken
            }
        } catch (e: Exception) {
            Log.e("Auth", "Refresh failed: ${e.message}")
            null
        }
    }


}