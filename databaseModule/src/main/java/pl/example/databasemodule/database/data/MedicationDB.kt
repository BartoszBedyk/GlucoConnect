package pl.example.databasemodule.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "medications")
data class MedicationDB(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: UUID,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "manufacturer")
    val manufacturer: String?,

    @ColumnInfo(name = "form")
    val form: String?,

    @ColumnInfo(name = "strength")
    val strength: String?
)
