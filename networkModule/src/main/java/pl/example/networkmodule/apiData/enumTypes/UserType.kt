package pl.example.networkmodule.apiData.enumTypes

import kotlinx.serialization.Serializable

@Serializable
enum class UserType{
    ADMIN, PATIENT, DOCTOR, OBSERVER
}