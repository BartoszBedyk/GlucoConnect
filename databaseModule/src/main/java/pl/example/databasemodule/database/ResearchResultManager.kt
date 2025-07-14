package pl.example.databasemodule.database

import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import pl.example.databasemodule.database.security.EncryptedKeyProvider

@RequiresApi(Build.VERSION_CODES.M)
object ResearchResultManager {
    @Volatile
    private var db: GlucoConnectMobileBase? = null


    fun getDatabase(context: Context): GlucoConnectMobileBase {

        return db ?: synchronized(this) {
            db ?: buildDatabase(context).also { db = it }
        }
    }


    private fun buildDatabase(context: Context): GlucoConnectMobileBase {
        SQLiteDatabase.loadLibs(context)
        val dbFile = context.getDatabasePath("GlucoConnect_mobileBase")
        val passphrase = EncryptedKeyProvider.getOrCreateDatabasePassphrase(context)
        val factory = SupportFactory(passphrase)

        return try {
            Room.databaseBuilder(
                context.applicationContext,
                GlucoConnectMobileBase::class.java,
                "GlucoConnect_mobileBase"
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration(true)
                .build()
        } catch (e: SQLiteException) {
            Log.e("ResearchResultManager", "Error opening database: ${e.message}")
            dbFile.delete()
            Room.databaseBuilder(
                context.applicationContext,
                GlucoConnectMobileBase::class.java,
                "GlucoConnect_mobileBase"
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
