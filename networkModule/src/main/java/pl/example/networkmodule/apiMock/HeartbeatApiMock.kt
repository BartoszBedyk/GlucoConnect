package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.apiMethods.HeartbeatApiInterface
import pl.example.networkmodule.requestData.CreateHeartbeatForm
import java.util.Date
import java.util.UUID

class HeartbeatApiMock : HeartbeatApiInterface {
    private val mockHeartbeats = mutableListOf<HeartbeatResult>()

    init {
        val userId = UUID.randomUUID()
        mockHeartbeats.addAll(
            listOf(
                HeartbeatResult(
                    id = UUID.randomUUID(),
                    userId = userId,
                    timestamp = Date(),
                    systolicPressure = 120,
                    diastolicPressure = 80,
                    pulse = 70,
                    note = "Regular checkup"
                ),
                HeartbeatResult(
                    id = UUID.randomUUID(),
                    userId = userId,
                    timestamp = Date(),
                    systolicPressure = 130,
                    diastolicPressure = 85,
                    pulse = 75,
                    note = "Post-exercise"
                )
            )
        )
    }

    override suspend fun createHeartbeat(heartbeat: CreateHeartbeatForm): Boolean {
        val newHeartbeat = HeartbeatResult(
            id = UUID.randomUUID(),
            userId = heartbeat.userId,
            timestamp = heartbeat.timestamp,
            systolicPressure = heartbeat.systolicPressure,
            diastolicPressure = heartbeat.diastolicPressure,
            pulse = heartbeat.pulse,
            note = heartbeat.note
        )
        mockHeartbeats.add(newHeartbeat)
        return true
    }

    override suspend fun getHeartBeat(id: String): HeartbeatResult? {
        return mockHeartbeats.find { it.id.toString() == id }
    }

    override suspend fun readHeartbeatForUser(userId: String): List<HeartbeatResult>? {
        return mockHeartbeats.filter { it.userId.toString() == userId }
    }

    override suspend fun deleteHeartbeat(id: String): Boolean {
        val heartbeat = mockHeartbeats.find { it.id.toString() == id }
        return if (heartbeat != null) {
            mockHeartbeats.remove(heartbeat)
            true
        } else {
            false
        }
    }

    override suspend fun deleteHeartbeatsForUser(userId: String): Boolean {
        val initialSize = mockHeartbeats.size
        mockHeartbeats.removeAll { it.userId.toString() == userId }
        return mockHeartbeats.size < initialSize
    }
}