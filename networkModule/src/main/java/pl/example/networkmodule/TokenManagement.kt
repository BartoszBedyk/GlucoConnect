package pl.example.networkmodule

import android.content.Context
import pl.example.networkmodule.apiMethods.ApiProvider

fun saveToken(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("jwt_token", token).apply()
}

fun getToken(context: Context): String? {
    if(ApiProvider.USE_MOCK_API) return ApiProvider.fakeToken
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getString("jwt_token", null)
}
