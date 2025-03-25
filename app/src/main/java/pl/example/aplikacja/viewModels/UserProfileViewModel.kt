package pl.example.aplikacja.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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

    init {
        fetchUserData()
        fetchUnaccepted()
        fetchAccepted()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _userData.value = userApi.getUserById(id = USER_ID)
        }
    }

    private fun fetchUnaccepted(){
        viewModelScope.launch {
            try{
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

}