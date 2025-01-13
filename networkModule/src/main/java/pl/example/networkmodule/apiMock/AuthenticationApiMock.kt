package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiMethods.ApiProvider
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.requestData.UserCredentials

class AuthenticationApiMock : AuthenticationApiInterface {
    private val mockUsers = mapOf(
        "b.b@wp.pl" to "Test123",
        "a.a@wp.pl" to "Test456"
    )

    override suspend fun login(userCredentials: UserCredentials): String? {
        val email = userCredentials.email
        val password = userCredentials.password


        return if (mockUsers[email] == password) {
            ApiProvider.fakeToken
        } else {
            null
        }
    }
}