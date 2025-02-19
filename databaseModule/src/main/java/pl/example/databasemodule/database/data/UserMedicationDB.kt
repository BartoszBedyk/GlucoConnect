package pl.example.databasemodule.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "user_medication", primaryKeys = ["user_id", "medication_id"])
data class UserMedicationDB(
    @ColumnInfo(name = "medication_id")
    val medicationId: UUID,

    @ColumnInfo(name = "user_id")
    val userId: UUID,

    @ColumnInfo(name = "dosage")
    val dosage: String,

    @ColumnInfo(name = "frequency")
    val frequency: String,

    @ColumnInfo(name = "start_date")
    val startDate: Date?,

    @ColumnInfo(name = "end_date")
    val endDate: Date?,

    @ColumnInfo(name = "notes")
    val notes: String?,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)
