package pl.example.aplikacja.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.example.aplikacja.JwtHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilHiltModule {

    @Provides
    @Singleton
    fun provideJwtHelper(@ApplicationContext context: Context): JwtHelper = JwtHelper(context)
}
