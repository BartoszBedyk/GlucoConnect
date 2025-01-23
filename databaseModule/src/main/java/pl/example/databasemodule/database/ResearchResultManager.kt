package pl.example.databasemodule.database

import android.content.Context
import androidx.room.Room
import pl.example.databasemodule.database.migration.MIGRATION_1_2
import pl.example.databasemodule.database.migration.MIGRATION_2_3
import pl.example.databasemodule.database.migration.MIGRATION_3_4
import pl.example.databasemodule.database.migration.MIGRATION_4_5
import pl.example.databasemodule.database.migration.MIGRATION_5_6

object ResearchResultManager {
    @Volatile
    private var db: ResearchResultDatabase? = null

    fun getDatabase(context: Context): ResearchResultDatabase {

        return db ?: synchronized(this) {
            db ?: buildDatabase(context).also { db = it }
        }
    }

    private fun buildDatabase(context: Context): ResearchResultDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ResearchResultDatabase::class.java,
            "research_results_database"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .fallbackToDestructiveMigration()
            .build()
    }
}
