package pl.example.databasemodule.database.Dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.example.databasemodule.database.data.ResearchResultDB

@Dao
interface ResearchResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(researchResult: ResearchResultDB)

    @Query("SELECT * FROM research_results WHERE user_id = :userId")
    suspend fun getResearchResultsForUser(userId: String): List<ResearchResultDB>

    @Query("SELECT * FROM research_results WHERE user_id = :userId LIMIT 3")
    suspend fun getResearchResultById(userId: String): ResearchResultDB?

    @Query("DELETE FROM research_results WHERE id = :id")
    suspend fun deleteResearchResult(id: String)

    @Query("SELECT * FROM research_results ORDER BY timestamp DESC LIMIT 3")
    suspend fun getLatestResearchResult(): List<ResearchResultDB?>

    @Query("UPDATE research_results SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM research_results WHERE is_synced = 0")
    suspend fun getUnsyncedResearchResults(): List<ResearchResultDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<ResearchResultDB>)


}