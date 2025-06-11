package pl.example.databasemodule.database.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "glucose_results")
data class GlucoseResultDB(
    @PrimaryKey(autoGenerate = false)
    val id: UUID,

    @ColumnInfo(name = "glucose_concentration")
    var glucoseConcentration: Double,

    @ColumnInfo(name = "glucose_unit")
    var unit: GlucoseUnitTypeDB,

    @ColumnInfo(name = "timestamp")
    val timestamp: Date,

    @ColumnInfo(name = "user_id")
    val userId: UUID?,

    @ColumnInfo(name = "deleted_on")
    val deletedOn: Date?,

    @ColumnInfo(name = "last_updated_on")
    val lastUpdatedOn: Date?,

    @ColumnInfo(name = "after_medication")
    val afterMedication: Boolean,

    @ColumnInfo(name = "empty_stomach")
    val emptyStomach: Boolean,

    @ColumnInfo(name = "notes")
    val notes: String?,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)


