package pl.example.aplikacja.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.example.networkmodule.KtorClient
import pl.example.networkmodule.apiMethods.AuthenticationApiInterface
import pl.example.networkmodule.apiMethods.HeartbeatApiInterface
import pl.example.networkmodule.apiMethods.MedicationApiInterface
import pl.example.networkmodule.apiMethods.ObserverApiInterface
import pl.example.networkmodule.apiMethods.ReportApiInterface
import pl.example.networkmodule.apiMethods.ResultApiInterface
import pl.example.networkmodule.apiMethods.UserApiInterface
import pl.example.networkmodule.apiMethods.UserMedicationApiInterface
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiProviderHiltModule {

    private const val USE_MOCK_API = false

    @Provides
    @Singleton
    fun provideKtorClient(@ApplicationContext context: Context): KtorClient = KtorClient(context)

    @Provides
    @Singleton
    fun provideResultApi(client: KtorClient): ResultApiInterface =
        if (USE_MOCK_API) ResultApiMock() else ResultApi(client)

    @Provides
    @Singleton
    fun provideUserApi(client: KtorClient): UserApiInterface = if (USE_MOCK_API) UserApiMock() else UserApi(client)

    @Provides
    @Singleton
    fun provideUserMedicationApi(client: KtorClient): UserMedicationApiInterface =
        if (USE_MOCK_API) UserMedicationApiMock() else UserMedicationApi(client)

    @Provides
    @Singleton
    fun provideHeartbeatApi(client: KtorClient): HeartbeatApiInterface =
        if (USE_MOCK_API) HeartbeatApiMock() else HeartbeatApi(client)

    @Provides
    @Singleton
    fun provideAuthenticationApi(client: KtorClient): AuthenticationApiInterface =
        if (USE_MOCK_API) AuthenticationApiMock() else AuthenticationApi(client)

    @Provides
    @Singleton
    fun provideMedicationApi(client: KtorClient): MedicationApiInterface =
        if (USE_MOCK_API) MedicationApiMock() else MedicationApi(client)

    @Provides
    @Singleton
    fun provideObserverApi(client: KtorClient): ObserverApiInterface =
        if (USE_MOCK_API) ObserverApiMock() else ObserverApi(client)

    @Provides
    @Singleton
    fun provideReportApi(client: KtorClient): ReportApiInterface =
        if (USE_MOCK_API) ReportApiMock() else ReportApi(client)
}
