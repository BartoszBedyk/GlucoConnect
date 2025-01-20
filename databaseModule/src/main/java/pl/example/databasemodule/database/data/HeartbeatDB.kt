package pl.example.databasemodule.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "heartbeat_results")
data class HeartbeatResultDB(
    @PrimaryKey(autoGenerate = false)
    val id: UUID,

    @ColumnInfo(name = "user_id")
    val userId: UUID,

    @ColumnInfo(name = "timestamp")
    val timestamp: Date,

    @ColumnInfo(name = "systolic_pressure")
    val systolicPressure: Int,

    @ColumnInfo(name = "diastolic_pressure")
    val diastolicPressure: Int,

    @ColumnInfo(name = "pulse")
    val pulse: Int,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false
)
