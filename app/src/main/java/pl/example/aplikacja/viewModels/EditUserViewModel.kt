package pl.example.aplikacja.viewModels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.auth0.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.example.aplikacja.removeQuotes
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.UserResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.getToken
import pl.example.networkmodule.requestData.UpdateUserNullForm
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor (@ApplicationContext private val context: Context) : ViewModel() {


    private val USER_ID: String = removeQuotes(JWT.decode(getToken(context)).getClaim("userId").toString())
    private val apiProvider = ApiProvider(context = context)
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
        Log.d("EditUserViewModel", "editUserData: $editData")
        return userApi.updateUserNulls(editData)
    }

    }
