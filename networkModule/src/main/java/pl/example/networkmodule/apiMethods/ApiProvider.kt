package pl.example.networkmodule.apiMethods


import android.content.Context
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiMock.AuthenticationApiMock
import pl.example.networkmodule.apiMock.HeartbeatApiMock
import pl.example.networkmodule.apiMock.MedicationApiMock
import pl.example.networkmodule.apiMock.ObserverApiMock
import pl.example.networkmodule.apiMock.ReportApiMock
import pl.example.networkmodule.apiMock.ResultApiMock
import pl.example.networkmodule.apiMock.UserApiMock
import pl.example.networkmodule.apiMock.UserMedicationApiMock
import pl.example.networkmodule.apis.AuthenticationApi
import pl.example.networkmodule.apis.HeartbeatApi
import pl.example.networkmodule.apis.MedicationApi
import pl.example.networkmodule.apis.ObserverApi
import pl.example.networkmodule.apis.ReportApi
import pl.example.networkmodule.apis.ResultApi
import pl.example.networkmodule.apis.UserApi
import pl.example.networkmodule.apis.UserMedicationApi


class ApiProvider(context: Context) {
    companion object{
        val USE_MOCK_API: Boolean = false    //true = mock
        val fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJteWF1ZGllbmNlIiwiaXNzIjoibXlpc3N1ZXIiLCJ1c2VySWQiOiIxZjUxMTg1MS1lMWU4LTQzNGEtODFjZS1lMmM4ZDYzNmY5N2IiLCJ1c2VybmFtZSI6ImIuYkB3cC5wbCIsInVzZXJUeXBlIjoiUEFUSUVOVCIsImV4cCI6MTc0ODc3Mzg1Mn0.yBhuPiAM4Xjty2L-EnSs9LeVl-U1Ilygst1TNwzIKMQ"
    }

    public val innerContext = context




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

    val observerApi: ObserverApiInterface by lazy {
        if (USE_MOCK_API) ObserverApiMock() else ObserverApi(
            KtorClient(context)
        )
    }

    val reportApi: ReportApiInterface by lazy {
        if (USE_MOCK_API) ReportApiMock() else ReportApi(
            KtorClient(context)
        )

    }


}