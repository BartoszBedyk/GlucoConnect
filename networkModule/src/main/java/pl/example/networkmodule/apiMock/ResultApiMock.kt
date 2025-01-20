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
            sequenceNumber = 1,
            glucoseConcentration = 25.5,
            unit = GlucoseUnitType.MMOL_PER_L,
            timestamp = Date(),
            userId = UUID.randomUUID(),
            deletedOn = null,
            lastUpdatedOn = Date()
        )
    }

    override suspend fun getAllResearchResults(): List<ResearchResult>? {
        return listOf(
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 1,
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = UUID.randomUUID(),
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 2,
                glucoseConcentration = 6.2,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = UUID.randomUUID(),
                deletedOn = null,
                lastUpdatedOn = Date()
            )
        )
    }

    override suspend fun getThreeResultsById(id: String): List<ResearchResult>? {
        val userId = UUID.randomUUID()
        return listOf(
            ResearchResult(
                id = UUID.fromString(id),
                sequenceNumber = 1,
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 2,
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 3,
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            )
        )
    }

    override suspend fun updateResearchResult(updateForm: ResearchResultUpdate): Boolean {
        return true
    }

    override suspend fun createResearchResult(createForm: ResearchResultCreate): Boolean {
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
                sequenceNumber = 1,
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 2,
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 3,
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.fromString(id),
                sequenceNumber = 1,
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 2,
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 3,
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.fromString(id),
                sequenceNumber = 1,
                glucoseConcentration = 5.5,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 2,
                glucoseConcentration = 4.8,
                unit = GlucoseUnitType.MMOL_PER_L,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            ),
            ResearchResult(
                id = UUID.randomUUID(),
                sequenceNumber = 3,
                glucoseConcentration = 126.1,
                unit = GlucoseUnitType.MG_PER_DL,
                timestamp = Date(),
                userId = userId,
                deletedOn = null,
                lastUpdatedOn = Date()
            )
        )
    }
}