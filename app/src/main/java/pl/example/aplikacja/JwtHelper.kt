package pl.example.aplikacja

import android.content.Context
import com.auth0.jwt.JWT
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.example.aplikacja.mappters.removeQuotes
import pl.example.networkmodule.getToken
import javax.inject.Inject

class JwtHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getUserId(): String =
        removeQuotes(JWT.decode(getToken(context)).getClaim("userId").toString())
}