package pl.example.aplikacja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.example.networkmodule.apiData.enumTypes.UserType

class BottomNavBarViewModelFactory(private val userType: UserType) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomNavBarViewModel::class.java)) {
            return BottomNavBarViewModel(userType) as T
        }
        throw IllegalArgumentException("Nieznany ViewModel")
    }
}