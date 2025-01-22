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
            note TEXT NOT NULL,
            is_synced INTEGER NOT NULL DEFAULT 0
        )
    """.trimIndent())
    }
}

val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS user_medication (
                user_id TEXT NOT NULL,
                medication_id TEXT NOT NULL,
                dosage TEXT NOT NULL,
                frequency TEXT NOT NULL,
                start_date INTEGER,
                end_date INTEGER,
                notes TEXT,
                medication_name TEXT NOT NULL,
                description TEXT,
                manufacturer TEXT,
                form TEXT,
                strength TEXT,
                is_synced INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(user_id, medication_id)
            )
        """.trimIndent())
    }
}

val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS medications (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT,
                form TEXT,
                strength TEXT,
                manufacturer TEXT
            )
        """.trimIndent())
    }
}



