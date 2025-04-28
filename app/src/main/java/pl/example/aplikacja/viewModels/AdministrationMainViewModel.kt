package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiMethods.ApiProvider

class AdministrationMainViewModel(apiProvider: ApiProvider) : ViewModel() {
    val userApi = apiProvider.userApi

    private val _users = MutableStateFlow<List<UserResult>>(emptyList())
    val users: MutableStateFlow<List<UserResult>> = _users

    private val authenticationApi = apiProvider.authenticationApi


    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy


    init {
        isApiAvilible(apiProvider.innerContext)


        viewModelScope.launch {
            healthy.collect { isHealthy ->
                if (isHealthy) {
                    fetchUsers()
                }
            }
        }
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                if (!healthy.value) throw IllegalStateException("API not available")
                _users.value = userApi.getAllUsers() ?: emptyList()
            } catch (
                e: Exception
            ) {
                _users.value = emptyList()
            }
        }
    }


    private var lastCheckedTime = 0L

    fun isApiAvilible(context: Context) {
        val now = System.currentTimeMillis()
        if (now - lastCheckedTime < 10_000) return
        lastCheckedTime = now

        viewModelScope.launch {
            try {
                val apiAvailable = authenticationApi.isApiAvlible()
                val networkAvailable = isNetworkAvailable(context)

                Log.d("HealthCheck", "API: $apiAvailable, Network: $networkAvailable")

                _healthy.value = apiAvailable == true && networkAvailable
                Log.d("HealthCheck", "Healthy: ${_healthy.value}")
            } catch (e: Exception) {
                Log.e("HealthCheck", "Error while checking health", e)
                _healthy.value = false
            }
        }
    }


}