package pl.example.networkmodule.apiMethods


import android.content.Context
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiMock.AuthenticationApiMock
import pl.example.networkmodule.apiMock.HeartbeatApiMock
import pl.example.networkmodule.apiMock.MedicationApiMock
import pl.example.networkmodule.apiMock.ResultApiMock
import pl.example.networkmodule.apiMock.UserApiMock
import pl.example.networkmodule.apiMock.UserMedicationApiMock
import pl.example.networkmodule.apis.AuthenticationApi
import pl.example.networkmodule.apis.HeartbeatApi
import pl.example.networkmodule.apis.MedicationApi
import pl.example.networkmodule.apis.ResultApi
import pl.example.networkmodule.apis.UserApi
import pl.example.networkmodule.apis.UserMedicationApi


class ApiProvider(context: Context) {
    companion object{
        val USE_MOCK_API: Boolean = false    //false = mock
        val fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJteWF1ZGllbmNlIiwiaXNzIjoibXlpc3N1ZXIiLCJ1c2VySWQiOiIwYWMwMjNlZC05YWUwLTQ0YzEtOWQyYy0zZmU1OGI2NzAxMTIiLCJ1c2VybmFtZSI6ImIuYkB3cC5wbCIsImV4cCI6MTczNjY4MzE3MH0.20HffuC0WtxdS8bsjFQKqm69YOIEMBGwLIUCvHTpVGE"
    }




    val resultApi: ResultApiInterface by lazy {
        if (USE_MOCK_API) ResultApiMock() else ResultApi(
            KtorClient(context)
        )
    }

    val userApi: UserApiInterface by lazy {
        if (USE_MOCK_API) UserApiMock() else UserApi(
            KtorClient(context)
        )
    }

    val userMedicationApi: UserMedicationApiInterface by lazy {
        if (USE_MOCK_API) UserMedicationApiMock() else UserMedicationApi(
            KtorClient(context)
        )
    }

    val heartbeatApi: HeartbeatApiInterface by lazy {
        if (USE_MOCK_API) HeartbeatApiMock() else HeartbeatApi(
            KtorClient(context)
        )
    }

    val authenticationApi: AuthenticationApiInterface by lazy {
        if (USE_MOCK_API) AuthenticationApiMock() else AuthenticationApi(
            KtorClient(context)
        )
    }

    val medicationApi: MedicationApiInterface by lazy {
        if (USE_MOCK_API) MedicationApiMock() else MedicationApi(
            KtorClient(context)
        )
    }


}