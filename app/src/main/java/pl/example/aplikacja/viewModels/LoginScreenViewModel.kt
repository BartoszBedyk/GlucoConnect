package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.UserCredentials
import pl.example.networkmodule.saveToken


class LoginScreenViewModel(private val apiProvider: ApiProvider): ViewModel() {
    private val authenticationApi = apiProvider.authenticationApi

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


}