package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable

@Serializable
data class CreateObserver(
    val observerId: String,
    val observedId: String
)