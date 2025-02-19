package pl.example.databasemodule.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.example.databasemodule.database.data.MedicationDB

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicationResult: MedicationDB)

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: String): MedicationDB?

    @Query("SELECT * FROM medications")
    suspend fun getAllMedications(): List<MedicationDB>

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteMedication(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<MedicationDB>)

}