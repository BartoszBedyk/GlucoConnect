package pl.example.databasemodule.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.example.databasemodule.database.Dao.HeartbeatDao
import pl.example.databasemodule.database.Dao.MedicationDao
import pl.example.databasemodule.database.Dao.ResearchResultDao
import pl.example.databasemodule.database.Dao.UserMedicationDao
import pl.example.databasemodule.database.data.HeartbeatDB
import pl.example.databasemodule.database.data.MedicationDB
import pl.example.databasemodule.database.data.ResearchResultDB
import pl.example.databasemodule.database.data.UserMedicationDB

@Database(entities = [ResearchResultDB::class, HeartbeatDB::class, UserMedicationDB::class, MedicationDB::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ResearchResultDatabase: RoomDatabase() {
    abstract val researchResultDao: ResearchResultDao
    abstract val heartbeatResultDao: HeartbeatDao
    abstract val userMedicationDao: UserMedicationDao
    abstract val medicationDao: MedicationDao

}
