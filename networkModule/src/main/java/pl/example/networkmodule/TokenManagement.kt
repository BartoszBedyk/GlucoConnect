package pl.example.networkmodule

import android.content.Context
import android.util.Log
import pl.example.networkmodule.apiMethods.ApiProvider

fun saveToken(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("jwt_token", token).apply()
}

fun getToken(context: Context): String? {
    Log.i("Token", "Get token")
    return try {
        if (ApiProvider.USE_MOCK_API) {
            ApiProvider.fakeToken
        } else {
            val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            sharedPreferences.getString("jwt_token", null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun clearToken(context: Context) {
    Log.i("Token", "Clear token")
    try {
        val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("jwt_token").apply()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


