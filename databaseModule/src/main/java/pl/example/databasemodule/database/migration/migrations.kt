package pl.example.databasemodule.database.migration

import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
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
    """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
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
        """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS medications (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT,
                form TEXT,
                strength TEXT,
                manufacturer TEXT
            )
        """.trimIndent()
        )
    }
}

val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_medication_new (
                medication_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                dosage TEXT NOT NULL,
                frequency TEXT NOT NULL,
                start_date INTEGER,
                end_date INTEGER,
                notes TEXT,
                is_synced INTEGER NOT NULL,
                PRIMARY KEY (user_id, medication_id)
            )
        """.trimIndent()
        )

        // 2. Przeniesienie danych ze starej tabeli do nowej tabeli
        db.execSQL(
            """
            INSERT INTO user_medication_new (
                medication_id, 
                user_id, 
                dosage, 
                frequency, 
                start_date, 
                end_date, 
                notes, 
                is_synced
            )
            SELECT 
                medication_id, 
                user_id, 
                dosage, 
                frequency, 
                start_date, 
                end_date, 
                notes, 
                is_synced
            FROM user_medication
        """.trimIndent()
        )

        // 3. Usunięcie starej tabeli
        db.execSQL("DROP TABLE user_medication")

        // 4. Zmiana nazwy nowej tabeli na starą
        db.execSQL("ALTER TABLE user_medication_new RENAME TO user_medication")
    }
}

val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pref_unit (
                user_id TEXT NOT NULL PRIMARY KEY,
                glucose_unit TEXT NOT NULL,
                is_synced INTEGER NOT NULL
            )
        """.trimIndent()
        )
    }
}


val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS glucose_results_new (
            id CHAR(36) PRIMARY KEY,
            glucose_concentration DOUBLE PRECISION NOT NULL,
            unit VARCHAR(30) NOT NULL,
            timestamp TIMESTAMP NOT NULL,
            empty_stomach BOOLEAN,
            after_medication BOOLEAN,
            notes TEXT,
            user_id CHAR(36), 
            deleted_on TIMESTAMP,
            last_updated_on TIMESTAMP
            )
        """.trimIndent()
        )

        // 2. Przeniesienie danych ze starej tabeli do nowej tabeli
        db.execSQL(
            """
            INSERT INTO glucose_results_new (
               id CHAR(36)
            glucose_concentration,
            unit,
            timestamp,
            empty_stomach,
            after_medication,
            notes,
            user_id, 
            deleted_on,
            last_updated_on
            )
            SELECT 
                      id CHAR(36)
            glucose_concentration,
            unit,
            timestamp,
            empty_stomach,
            after_medication,
            notes,
            user_id, 
            deleted_on,
            last_updated_on
            FROM research_results
        """.trimIndent()
        )

        // 3. Usunięcie starej tabeli
        db.execSQL("DROP TABLE research_results")

        // 4. Zmiana nazwy nowej tabeli na starą
        db.execSQL("ALTER TABLE glucose_results_new RENAME TO glucose_results")
    }
}




