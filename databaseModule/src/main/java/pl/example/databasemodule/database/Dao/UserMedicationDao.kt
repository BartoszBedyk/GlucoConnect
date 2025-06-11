package pl.example.databasemodule.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.example.databasemodule.database.data.MedicationDB
import pl.example.databasemodule.database.data.UserMedicationDB
import pl.example.networkmodule.apiData.UserMedicationResult

@Dao
interface UserMedicationDao {
    @Insert
    suspend fun insert(userMedicationResult: UserMedicationDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userMedicationResult: List<UserMedicationDB>)

    @Query("SELECT * FROM user_medication WHERE user_id = :userId")
    suspend fun getMedicationsForUser(userId: String): List<UserMedicationDB>

    @Query("DELETE FROM user_medication WHERE medication_id = :medicationId")
    suspend fun deleteMedication(medicationId: String)

    @Query("SELECT * FROM user_medication WHERE user_id = :userId ORDER BY start_date DESC")
    suspend fun getLatestMedicationsForUser(userId: String): List<UserMedicationDB>

    @Query("SELECT * FROM user_medication WHERE user_id = :userId AND medication_id = :medicationId")
    suspend fun getMedicationById(userId: String, medicationId: String): UserMedicationDB?

    @Query("""
    SELECT 
        um.user_id AS userId, 
        um.medication_id AS medicationId, 
        um.dosage, 
        um.frequency, 
        um.start_date AS startDate, 
        um.end_date AS endDate, 
        um.notes,
        m.name AS medicationName,
        m.description,
        m.manufacturer,
        m.form,
        m.strength
    FROM user_medication um
    INNER JOIN medications m ON um.medication_id = m.id
    WHERE um.user_id = :userId
    AND (um.start_date IS NULL OR um.start_date <= DATE('now'))
    AND (um.end_date IS NULL OR um.end_date >= DATE('now'))
""")
    suspend fun getCurrentMedicationsForUser(userId: String): List<UserMedicationResult>

}