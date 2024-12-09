package pl.example.networkmodule

import android.content.Context

fun saveToken(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("jwt_token", token).apply()
}

fun getToken(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    return sharedPreferences.getString("jwt_token", null)
}
