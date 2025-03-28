package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.requestData.UserCredentials

interface AuthenticationApiInterface {
    suspend fun login(userCredentials: UserCredentials): String?
    suspend fun refreshToken(token: String): String?
    suspend fun isApiAvlible(): Boolean?
}