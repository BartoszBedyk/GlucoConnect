package pl.example.aplikacja.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.ObserverResult
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiMethods.ApiProvider

class ObserverMainScreenViewModel(context: Context, private val OBSERVER_ID: String): ViewModel() {
    private val apiProvider = ApiProvider(context)

    private val observerApi = apiProvider.observerApi
    private val userApi = apiProvider.userApi

    private val _observedAccepted = MutableStateFlow<List<ObserverResult>>(emptyList())
    val observedAccepted: StateFlow<List<ObserverResult>> = _observedAccepted

    private val _observedUnaccepted = MutableStateFlow<List<ObserverResult>>(emptyList())
    val observedUnaccepted: StateFlow<List<ObserverResult>> = _observedUnaccepted

    private var _observedAcceptedUser = MutableStateFlow<List<UserResult>>(emptyList())
    val observedAcceptedUser: StateFlow<List<UserResult>> = _observedAcceptedUser

    private val _observedUnacceptedUser = MutableStateFlow<List<UserResult>>(emptyList())
    val observedUnacceptedUser: StateFlow<List<UserResult>> = _observedUnacceptedUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        fetchObservedData()
    }

    fun fetchObservedData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val accepted = observerApi.getObservedAcceptedByObserverId(OBSERVER_ID) ?: emptyList()
                val unAccepted = observerApi.getObservedUnAcceptedByObserverId(OBSERVER_ID) ?: emptyList()

                _observedAccepted.value = accepted
                _observedUnaccepted.value = unAccepted


                if (accepted.isNotEmpty()) {
                    val users = accepted.mapNotNull { observed ->
                        userApi.getUserById(observed.observedId.toString())
                    }
                    _observedAcceptedUser.value = users
                }
            } catch (e: Exception) {
                _observedAccepted.value = emptyList()
                _observedUnaccepted.value = emptyList()
                _observedAcceptedUser.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }




}