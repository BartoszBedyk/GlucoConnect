package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.apiMethods.ResultApiInterface
import pl.example.networkmodule.requestData.ResearchResultCreate
import pl.example.networkmodule.requestData.ResearchResultUpdate
import java.util.Date
import java.util.UUID

class ResultApiMock: ResultApiInterface {
    override suspend fun getResearchResultsById(id: String): ResearchResult? {
        return ResearchResult(
            id = UUID.fromString(id),
            glucoseConcentration = 25.5,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = UUID.randomUUID(),
            deletedOn = null,
            lastUpdatedOn = Date(),
            afterMedication = false,
            emptyStomach = false,
            notes = ""
        )
    }

    override suspend fun getAllResearchResults(): List<ResearchResult>? {
        return listOf(
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = UUID.randomUUID(),
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 6.2,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = UUID.randomUUID(),
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            )
        )
    }

    override suspend fun getThreeResultsById(id: String): List<ResearchResult>? {
        val userId = UUID.randomUUID()
        return listOf(
            ResearchResult(
                id = UUID.fromString(id),
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            )
        )
    }

    override suspend fun updateResearchResult(updateForm: ResearchResultUpdate): Boolean {
        return true
    }

    override suspend fun createResearchResult(createForm: ResearchResultCreate): String? {
        return UUID.randomUUID().toString()
    }

    override suspend fun syncResult(researchResult: ResearchResult): Boolean {
        return true
    }

    override suspend fun deleteResearchResult(id: String): Boolean {
        return true
    }

    override suspend fun safeDeleteResearchResult(id: String): Boolean {
        return true
    }

    override suspend fun getResultsByUserId(id: String): List<ResearchResult>? {
        val userId = UUID.randomUUID()
        return listOf(
            ResearchResult(
                id = UUID.fromString(id),
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""

            ),
            ResearchResult(
                id = UUID.fromString(id),
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""

            ),
            ResearchResult(
                id = UUID.fromString(id),
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date(),
                afterMedication = false,
                emptyStomach = false,
                notes = ""

            )
        )
    }

    override suspend fun getHb1AcResultById(id: String): Float? {
        return 6.0f
    }
}