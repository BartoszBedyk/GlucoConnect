package pl.example.aplikacja.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.Screens.isNetworkAvailable
import pl.example.networkmodule.apiData.ObserverResult
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.CreateObserver
import java.math.RoundingMode

class UserProfileViewModel(apiProvider: ApiProvider, private val USER_ID: String) : ViewModel() {

    private val userApi = apiProvider.userApi
    private val observerApi = apiProvider.observerApi

    private val _userData = MutableStateFlow<UserResult?>(null)
    val userData: MutableStateFlow<UserResult?> = _userData

    private val _obsered = MutableStateFlow<UserResult?>(null)
    val observed: MutableStateFlow<UserResult?> = _obsered

    private val _obseredUser = MutableStateFlow<UserResult?>(null)
    val observedUser: MutableStateFlow<UserResult?> = _obseredUser

    private val _observatorsAccepted = MutableStateFlow<List<UserResult>?>(emptyList())
    val observatorsAccepted: MutableStateFlow<List<UserResult>?> = _observatorsAccepted

    private val _observatorsUnAccepted = MutableStateFlow<List<UserResult>?>(emptyList())
    val observatorsUnAccepted: MutableStateFlow<List<UserResult>?> = _observatorsUnAccepted

    private val authenticationApi = apiProvider.authenticationApi


    private val _healthy = MutableStateFlow<Boolean>(false)
    val healthy: StateFlow<Boolean> = _healthy


    init {
        isApiAvilible(apiProvider.innerContext)
        fetchUserData()
        fetchUnaccepted()
        fetchAccepted()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            try{
                if (!healthy.value) throw IllegalStateException("API not available")
                _userData.value = userApi.getUserById(id = USER_ID)
            }catch (e: Exception){
                println(e.message)
            }
        }
    }

    private fun fetchUnaccepted(){
        viewModelScope.launch {
            try{
                if (!healthy.value) throw IllegalStateException("API not available")
                val unAccepted  = observerApi.getObservatorByObservedIdUnAccepted(USER_ID)
                if (unAccepted != null) {
                    if (unAccepted.isNotEmpty()) {
                        Log.e("UnAccepted", unAccepted.toString())
                        val users = unAccepted.mapNotNull { observed ->
                            userApi.getUserById(observed.observerId.toString())
                        }
                        _observatorsUnAccepted.value = users
                    }
                }
                val accepted = observerApi.getObservatorByObservedIdAccepted(USER_ID)
                if (accepted != null) {
                    if (accepted.isNotEmpty()) {
                        Log.e("Accepted", unAccepted.toString())
                        val users = accepted.mapNotNull { observed ->
                            userApi.getUserById(observed.observerId.toString())
                        }
                        _observatorsAccepted.value = users
                    }
                }
            }catch(e : Exception){
                println(e.message)
            }
        }
    }

    private fun fetchAccepted(){
        viewModelScope.launch {
            try{
                if (!healthy.value) throw IllegalStateException("API not available")
            }catch(e : Exception){
                println(e.message)
            }
        }
    }

    fun observe(partOne: String, partTwo: String) {
        Log.e("Dialog", partOne + partTwo)
        viewModelScope.launch {
            try{
                val result = userApi.observe(partOne, partTwo)
                Log.e("Dialog", "UUID:" + result.toString())
                _obsered.value = result
            }catch (e: Exception){
                println(e.message)
            }
        }
    }

    fun observeUser(observerId: String, observedId: String){
        viewModelScope.launch {
            try{
                val result = observerApi.observe(CreateObserver(observerId, observedId))
                _obseredUser.value = userApi.getUserById(observedId)
            }catch (e: Exception){
                println(e.message)
            }
        }

    }

    fun accept(observerId: String, observedId: String) {
        viewModelScope.launch {
            try {
                observerApi.acceptObservation(CreateObserver(observerId, observedId))
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun unAccept(observerId: String, observedId: String) {
        viewModelScope.launch {
            try {
                observerApi.unAcceptObservation(CreateObserver(observerId, observedId))
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    var lastCheckedTime = 0L
    private fun isApiAvilible(context: Context) {
        val now = System.currentTimeMillis()
        if (now - lastCheckedTime < 10_000) return
        lastCheckedTime = now

        viewModelScope.launch {
            try {
                _healthy?.value =
                    authenticationApi.isApiAvlible() == true && isNetworkAvailable(context)
            } catch (e: Exception) {
                _healthy?.value = false
            }
        }
    }

}