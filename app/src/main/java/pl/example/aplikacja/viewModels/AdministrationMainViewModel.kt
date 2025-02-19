package pl.example.aplikacja.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiMethods.ApiProvider

class AdministrationMainViewModel(apiProvider: ApiProvider) : ViewModel() {
    val userApi = apiProvider.userApi

    private val _users = MutableStateFlow<List<UserResult>>(emptyList())
    val users: MutableStateFlow<List<UserResult>> = _users


    init{
        fetchUsers()
    }

    private fun fetchUsers(){
        viewModelScope.launch {
            _users.value = userApi.getAllUsers() ?: emptyList()
        }
    }



}