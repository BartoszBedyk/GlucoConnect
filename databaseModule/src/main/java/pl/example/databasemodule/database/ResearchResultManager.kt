package pl.example.databasemodule.database

import android.content.Context
import androidx.room.Room
import pl.example.databasemodule.database.migration.MIGRATION_1_2
import pl.example.databasemodule.database.migration.MIGRATION_2_3
import pl.example.databasemodule.database.migration.MIGRATION_3_4
import pl.example.databasemodule.database.migration.MIGRATION_4_5
import pl.example.databasemodule.database.migration.MIGRATION_5_6
import pl.example.databasemodule.database.migration.MIGRATION_6_7

object ResearchResultManager {
    @Volatile
    private var db: GlucoConnectMobileBase? = null

    fun getDatabase(context: Context): GlucoConnectMobileBase {

        return db ?: synchronized(this) {
            db ?: buildDatabase(context).also { db = it }
        }
    }

    private fun buildDatabase(context: Context): GlucoConnectMobileBase {
        return Room.databaseBuilder(
            context.applicationContext,
            GlucoConnectMobileBase::class.java,
            "GlucoConnect_mobileBase"
        )
            .fallbackToDestructiveMigration()
            //.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .fallbackToDestructiveMigration()
            .build()
    }
}
