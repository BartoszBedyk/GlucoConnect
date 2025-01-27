package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiData.MedicationResult
import pl.example.networkmodule.apiMethods.MedicationApiInterface
import pl.example.networkmodule.requestData.CreateMedication
import java.util.UUID

class MedicationApiMock: MedicationApiInterface {
    private val mockMedications = mutableListOf<MedicationResult>()

    init {
        mockMedications.addAll(
            listOf(
                MedicationResult(
                    id = UUID.randomUUID(),
                    name = "Paracetamol",
                    description = "Pain reliever and fever reducer.",
                    manufacturer = "Pharma Inc.",
                    form = "Tablet",
                    strength = "500mg"
                ),
                MedicationResult(
                    id = UUID.randomUUID(),
                    name = "Ibuprofen",
                    description = "Anti-inflammatory drug.",
                    manufacturer = "Health Corp.",
                    form = "Capsule",
                    strength = "200mg"
                )
            )
        )
    }

    override suspend fun createMedication(medication: CreateMedication): Boolean {
        val newMedication = MedicationResult(
            id = UUID.randomUUID(),
            name = medication.name,
            description = medication.description,
            manufacturer = medication.manufacturer,
            form = medication.form,
            strength = medication.strength
        )
        mockMedications.add(newMedication)
        return true
    }

    override suspend fun readMedication(id: String): MedicationResult? {
        return mockMedications.find { it.id.toString() == id }
    }

    override suspend fun getAllMedications(): List<MedicationResult>? {
        return listOf(
            MedicationResult(
                id = UUID.randomUUID(),
                name = "Śmiercionka Memcena",
                description = "Pain reliever and fever reducer.",
                manufacturer = "Pharma Inc.",
                form = "Tablet",
                strength = "500mg"
            ),
            MedicationResult(
                id = UUID.randomUUID(),
                name = "Ibuprofen",
                description = "Anti-inflammatory drug.",
                manufacturer = "Health Corp.",
                form = "Capsule",
                strength = "200mg"
            ),
            MedicationResult(
                id = UUID.randomUUID(),
                name = "Ibuprofen",
                description = "Anti-inflammatory drug.",
                manufacturer = "Health Corp.",
                form = "Capsule",
                strength = "500mg"
            ),
            MedicationResult(
                id = UUID.randomUUID(),
                name = "Ibuprofen max",
                description = "Anti-inflammatory drug.",
                manufacturer = "Health Corp.",
                form = "Capsule",
                strength = "200mg"
            ),
            MedicationResult(
                id = UUID.randomUUID(),
                name = "Apap",
                description = "Anti-inflammatory drug.",
                manufacturer = "Health Corp.",
                form = "Capsule",
                strength = "200mg"
            ),
            MedicationResult(
                id = UUID.randomUUID(),
                name = "Deksztusan",
                description = "Anti-inflammatory drug.",
                manufacturer = "Health Corp.",
                form = "Capsule",
                strength = "200mg"
            )
        )
    }

    override suspend fun deleteMedication(id: String): Boolean {
        val medication = mockMedications.find { it.id.toString() == id }
        return if (medication != null) {
            mockMedications.remove(medication)
            true
        } else {
            false
        }
    }

    override suspend fun getUnsynced(userId: String): List<MedicationResult>? {
        return null
    }
}