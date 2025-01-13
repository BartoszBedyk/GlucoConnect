package pl.example.aplikacja.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.requestData.UpdateUserNullForm
import java.math.RoundingMode

class EditUserViewModel(apiProvider: ApiProvider, private val USER_ID: String) : ViewModel() {

    private val userApi = apiProvider.userApi

    private val _userData = MutableStateFlow<UserResult?>(null)
    val userData: MutableStateFlow<UserResult?> = _userData

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _userData.value = userApi.getUserById(id = USER_ID)
        }
    }

    suspend fun editUserData(editData: UpdateUserNullForm): Boolean {
        return userApi.updateUserNulls(editData)
    }

    }
