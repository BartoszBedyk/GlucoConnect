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

    @Query("SELECT * FROM research_results WHERE user_id = :userId ORDER BY timestamp DESC")
    suspend fun getResearchResultsForUser(userId: String): List<ResearchResultDB>

    @Query("SELECT * FROM research_results WHERE id = :id")
    suspend fun getResearchResultById(id: String): ResearchResultDB?

    @Query("DELETE FROM research_results WHERE id = :id")
    suspend fun deleteResearchResult(id: String)

    @Query("SELECT * FROM research_results WHERE user_id = :userId ORDER BY timestamp DESC LIMIT 3")
    suspend fun getLatestThreeResearchResult(userId: String): List<ResearchResultDB>

    @Query("UPDATE research_results SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM research_results WHERE is_synced = 0")
    suspend fun getUnsyncedResearchResults(): List<ResearchResultDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<ResearchResultDB>)


}