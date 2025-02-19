package pl.example.databasemodule.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.example.databasemodule.database.data.PrefUnitDB

@Dao
interface PrefUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: PrefUnitDB)

    @Query("SELECT glucose_unit FROM pref_unit WHERE user_id = :userId")
    suspend fun getUnitByUserId(userId: String): String?

    @Query("UPDATE pref_unit SET glucose_unit = :glucoseUnit WHERE user_id = :userId")
    suspend fun updateGlucoseUnit(userId: String, glucoseUnit: String)

    @Query("SELECT * FROM pref_unit WHERE is_synced = 0")
    suspend fun getAllNotSynced() : PrefUnitDB

    @Query("UPDATE pref_unit SET is_synced = 1 WHERE user_id = :userId")
    suspend fun sync(userId: String)
}