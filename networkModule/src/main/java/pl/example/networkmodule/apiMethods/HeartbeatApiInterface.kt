package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.HeartbeatResult
import pl.example.networkmodule.requestData.CreateHeartbeatForm

interface HeartbeatApiInterface {
    suspend fun createHeartbeat(heartbeat: CreateHeartbeatForm): Boolean
    suspend fun getHeartBeat(id: String): HeartbeatResult?
    suspend fun readHeartbeatForUser(userId: String): List<HeartbeatResult>?
    suspend fun deleteHeartbeat(id: String): Boolean
    suspend fun deleteHeartbeatsForUser(userId: String): Boolean
}