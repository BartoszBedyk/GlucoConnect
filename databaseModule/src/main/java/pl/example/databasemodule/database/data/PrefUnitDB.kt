package pl.example.databasemodule.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pref_unit")
data class PrefUnitDB(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "glucose_unit")
    val glucoseUnit: String,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean,

    @ColumnInfo(name = "diabetes_type")
    val diabetesType: DiabetesTypeDB

)