package pl.example.networkmodule.apiData.enumTypes

import kotlinx.serialization.Serializable

@Serializable
enum class DiabetesType{
    TYPE_1, TYPE_2, MODY, LADA, GESTATIONAL, NONE
}