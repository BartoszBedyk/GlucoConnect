package pl.example.databasemodule.database.migration

import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS heartbeat_results (
                id TEXT NOT NULL PRIMARY KEY,
                user_id TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                systolic_pressure INTEGER NOT NULL,
                diastolic_pressure INTEGER NOT NULL,
                pulse INTEGER NOT NULL,
                note TEXT NOT NULL
            )
        """.trimIndent())
    }
}
