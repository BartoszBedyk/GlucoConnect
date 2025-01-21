package pl.example.databasemodule.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pl.example.databasemodule.database.data.UserMedicationDB

@Dao
interface UserMedicationDao {
    @Insert
    suspend fun insert(userMedicationResult: UserMedicationDB)

    @Query("SELECT * FROM user_medication WHERE user_id = :userId")
    suspend fun getMedicationsForUser(userId: String): List<UserMedicationDB>

    @Query("DELETE FROM user_medication WHERE medication_id = :medicationId")
    suspend fun deleteMedication(medicationId: String)

    @Query("SELECT * FROM user_medication WHERE user_id = :userId ORDER BY start_date DESC")
    suspend fun getLatestMedicationsForUser(userId: String): List<UserMedicationDB>

    @Query("SELECT * FROM user_medication WHERE user_id = :userId AND medication_id = :medicationId")
    suspend fun getMedicationById(userId: String, medicationId: String): UserMedicationDB?

}