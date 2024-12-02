package pl.example.networkmodule.requestData

import kotlinx.serialization.Serializable

@Serializable
data class CreateMedication(
    val name : String,
    val description  : String,
    val manufacturer : String,
    val form : String,
    val strength : String
)
