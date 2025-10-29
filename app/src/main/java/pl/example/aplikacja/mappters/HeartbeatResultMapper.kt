package pl.example.aplikacja.mappters

import pl.example.databasemodule.database.data.HeartbeatDB
import pl.example.networkmodule.apiData.HeartbeatResult

fun HeartbeatDB.toHeartbeatResult(): HeartbeatResult = this.let { dbResult ->
    HeartbeatResult(
        id = dbResult.id,
        userId = dbResult.userId,
        timestamp = dbResult.timestamp,
        systolicPressure = dbResult.systolicPressure,
        diastolicPressure = dbResult.diastolicPressure,
        pulse = dbResult.pulse,
        note = dbResult.note,
    )
}

fun HeartbeatResult.toHeartbeatResultDB(): HeartbeatDB = this.let { dbResult ->
    HeartbeatDB(
        id = dbResult.id,
        userId = dbResult.userId,
        timestamp = dbResult.timestamp,
        systolicPressure = dbResult.systolicPressure,
        diastolicPressure = dbResult.diastolicPressure,
        pulse = dbResult.pulse,
        note = dbResult.note,
    )
}

fun List<HeartbeatResult>.toHeartbeatDB(): List<HeartbeatDB> = this.map { it.toHeartbeatResultDB() }

fun List<HeartbeatDB>.toHeartbeatResultList(): List<HeartbeatResult> = this.map { it.toHeartbeatResult() }
