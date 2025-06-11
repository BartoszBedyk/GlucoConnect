package pl.example.databasemodule.database.Dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.example.databasemodule.database.data.GlucoseResultDB

@Dao
interface GlucoseResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(researchResult: GlucoseResultDB)

    @Query("SELECT * FROM glucose_results WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getResearchResultsForUser(userId: String): List<GlucoseResultDB>

    @Query("SELECT * FROM glucose_results WHERE id = :id")
    suspend fun getResearchResultById(id: String): GlucoseResultDB?

    @Query("DELETE FROM glucose_results WHERE id = :id")
    suspend fun deleteResearchResult(id: String)

    @Query("SELECT * FROM glucose_results WHERE user_id = :userId ORDER BY timestamp DESC LIMIT 3")
    suspend fun getLatestThreeResearchResult(userId: String): List<GlucoseResultDB>

    @Query("UPDATE glucose_results SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM glucose_results WHERE is_synced = 0")
    suspend fun getUnsyncedResearchResults(): List<GlucoseResultDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<GlucoseResultDB>)


}