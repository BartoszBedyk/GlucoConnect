package pl.example.databasemodule.database

import android.content.Context
import androidx.room.Room
import pl.example.databasemodule.database.migration.MIGRATION_1_2

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
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }
}
