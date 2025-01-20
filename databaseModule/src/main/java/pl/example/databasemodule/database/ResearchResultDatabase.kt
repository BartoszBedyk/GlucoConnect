package pl.example.databasemodule.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.example.databasemodule.database.Dao.HeartbeatResultDao
import pl.example.databasemodule.database.Dao.ResearchResultDao
import pl.example.databasemodule.database.data.HeartbeatResultDB
import pl.example.databasemodule.database.data.ResearchResultDB

@Database(entities = [ResearchResultDB::class, HeartbeatResultDB::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ResearchResultDatabase: RoomDatabase() {
    abstract val researchResultDao: ResearchResultDao
    abstract val heartbeatResultDao: HeartbeatResultDao

}
