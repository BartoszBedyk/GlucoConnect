package pl.example.databasemodule.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.example.databasemodule.database.data.HeartbeatDB

@Dao
interface HeartbeatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(heartbeatResult: HeartbeatDB)

    @Query("SELECT * FROM heartbeat_results WHERE user_id = :userId")
    suspend fun getHeartbeatResultsForUser(userId: String): List<HeartbeatDB>

    @Query("SELECT * FROM heartbeat_results WHERE id = :id")
    suspend fun getHeartbeatResultById(id: String): HeartbeatDB?

    @Query("DELETE FROM heartbeat_results WHERE id = :id")
    suspend fun deleteHeartbeatResult(id: String)

    @Query("SELECT * FROM heartbeat_results ORDER BY timestamp DESC LIMIT 3")
    suspend fun getLatestHeartbeatResult(): List<HeartbeatDB?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<HeartbeatDB>)

    @Query("UPDATE heartbeat_results SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM heartbeat_results WHERE is_synced = 0")
    suspend fun getUnsyncedHeartbeatResults(): List<HeartbeatDB>
}
