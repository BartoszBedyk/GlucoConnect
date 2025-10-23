package pl.example.aplikacja.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.example.databasemodule.database.repository.GlucoseResultRepository
import pl.example.databasemodule.database.repository.HeartbeatRepository
import pl.example.databasemodule.database.repository.PrefUnitRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseProviderHiltModule {

    @Provides
    @Singleton
    fun provideGlucoseResultRepository(@ApplicationContext context: Context): GlucoseResultRepository =
        GlucoseResultRepository(context)

    @Provides
    @Singleton
    fun providePrefUnitRepository(@ApplicationContext context: Context): PrefUnitRepository =
        PrefUnitRepository(context)

    @Provides
    @Singleton
    fun provideHeartbeatResultRepository(@ApplicationContext context: Context): HeartbeatRepository =
        HeartbeatRepository(context)
}