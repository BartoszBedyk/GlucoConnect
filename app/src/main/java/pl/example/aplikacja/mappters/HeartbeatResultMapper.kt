package pl.example.aplikacja.mappters

import pl.example.databasemodule.database.data.HeartbeatDB
import pl.example.networkmodule.apiData.HeartbeatResult

fun HeartbeatDB.toHeartbeatResult(): HeartbeatResult {
    return this.let { dbResult ->
        HeartbeatResult(
            id = dbResult.id,
            userId = dbResult.userId,
            timestamp = dbResult.timestamp,
            systolicPressure = dbResult.systolicPressure,
            diastolicPressure = dbResult.diastolicPressure,
            pulse = dbResult.pulse,
            note = dbResult.note
        )
    }
}

fun HeartbeatResult.toHeartbeatResultDB(): HeartbeatDB {
    return this.let { dbResult ->
        HeartbeatDB(
            id = dbResult.id,
            userId = dbResult.userId,
            timestamp = dbResult.timestamp,
            systolicPressure = dbResult.systolicPressure,
            diastolicPressure = dbResult.diastolicPressure,
            pulse = dbResult.pulse,
            note = dbResult.note
        )
    }
}

fun List<HeartbeatResult>.toHeartbeatDB(): List<HeartbeatDB> {
    return this.map { it.toHeartbeatResultDB() }
}

fun List<HeartbeatDB>.toHeartbeatResultList(): List<HeartbeatResult> {
    return this.map { it.toHeartbeatResult() }
}