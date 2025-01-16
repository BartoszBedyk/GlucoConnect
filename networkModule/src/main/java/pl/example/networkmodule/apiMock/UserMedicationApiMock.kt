package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiData.UserMedicationResult
import pl.example.networkmodule.apiMethods.UserMedicationApiInterface
import pl.example.networkmodule.requestData.CreateUserMedicationForm
import java.util.Date
import java.util.UUID

class UserMedicationApiMock : UserMedicationApiInterface {
    private val mockUserMedications = mutableListOf<UserMedicationResult>()

    init {
        val userId = UUID.randomUUID()
        val medicationId = UUID.randomUUID()
        mockUserMedications.addAll(
            listOf(
                UserMedicationResult(
                    userId = userId,
                    medicationId = medicationId,
                    dosage = "1 tablet",
                    frequency = "Twice a day",
                    startDate = Date(),
                    endDate = null,
                    notes = "Take after meals",
                    medicationName = "Ibuprofen",
                    description = "Anti-inflammatory drug.",
                    manufacturer = "Health Pharma",
                    form = "Capsule",
                    strength = "200mg"
                ),
                UserMedicationResult(
                    userId = userId,
                    medicationId = UUID.randomUUID(),
                    dosage = "2 tablets",
                    frequency = "Once a day",
                    startDate = Date(),
                    endDate = Date(),
                    notes = null,
                    medicationName = "Paracetamol",
                    description = "Pain reliever and fever reducer.",
                    manufacturer = "Pharma Inc.",
                    form = "Tablet",
                    strength = "500mg"
                )
            )
        )
    }

    override suspend fun createUserMedication(userMedication: CreateUserMedicationForm): Boolean {
        val newUserMedication = UserMedicationResult(
            userId = userMedication.userId,
            medicationId = userMedication.medicationId,
            dosage = userMedication.dosage,
            frequency = userMedication.frequency,
            startDate = userMedication.startDate,
            endDate = userMedication.endDate,
            notes = userMedication.notes,
            medicationName = "Mock Medication Name",
            description = "Mock Description",
            manufacturer = "Mock Manufacturer",
            form = "Mock Form",
            strength = "Mock Strength"
        )
        mockUserMedications.add(newUserMedication)
        return true
    }

    override suspend fun readUserMedication(id: String): UserMedicationResult? {
        return mockUserMedications.find { it.medicationId.toString() == id }
    }

    override suspend fun deleteUserMedication(id: String): Boolean {
        val medication = mockUserMedications.find { it.medicationId.toString() == id }
        return if (medication != null) {
            mockUserMedications.remove(medication)
            true
        } else {
            false
        }
    }

    override suspend fun deleteUserMedicationForUser(userId: String): Boolean {
        val initialSize = mockUserMedications.size
        mockUserMedications.removeAll { it.userId.toString() == userId }
        return mockUserMedications.size < initialSize
    }

    override suspend fun readTodayUserMedication(id: String): List<UserMedicationResult>? {
        return listOf(
            UserMedicationResult(
                userId = UUID.randomUUID(),
                medicationId = UUID.randomUUID(),
                dosage = "500mg",
                frequency = "Twice a day",
                startDate = Date(),
                endDate = null,
                notes = "Take after meals",
                medicationName = "Aspirin",
                description = "Pain reliever",
                manufacturer = "Pharma Inc.",
                form = "Tablet",
                strength = "500mg"
            ),
            UserMedicationResult(
                userId = UUID.randomUUID(),
                medicationId = UUID.randomUUID(),
                dosage = "250mg",
                frequency = "Once a day",
                startDate = Date(),
                endDate = null,
                notes = "Take in the morning",
                medicationName = "Ibuprofen",
                description = "Anti-inflammatory",
                manufacturer = "Health Corp.",
                form = "Capsule",
                strength = "250mg"
            )
        )
    }
}