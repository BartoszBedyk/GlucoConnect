package pl.example.bluetoothmodule

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.example.bluetoothmodule.data.AndroidBluetoothController
import pl.example.bluetoothmodule.domain.BluetoothController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
    @Provides
    @Singleton
    fun providesBluetoothController(@ApplicationContext context: Context): BluetoothController{
        return AndroidBluetoothController(context)
    }
}