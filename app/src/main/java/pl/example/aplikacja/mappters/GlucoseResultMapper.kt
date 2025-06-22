package pl.example.aplikacja.mappters

import pl.example.databasemodule.database.data.GlucoseResultDB
import pl.example.databasemodule.database.data.GlucoseUnitTypeDB
import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.apiData.enumTypes.GlucoseUnitType
import pl.example.networkmodule.requestData.ResearchResultCreate
import java.util.Date
import java.util.UUID

fun GlucoseResultDB.toResearchResult(): ResearchResult {
    return this.let {
        ResearchResult(
            id = it.id,
            glucoseConcentration = it.glucoseConcentration,
            unit = it.unit.toGlucoseUnitType(),
            timestamp = it.timestamp,
            userId = it.userId,
            deletedOn = it.deletedOn,
            lastUpdatedOn = it.lastUpdatedOn,
            afterMedication = it.afterMedication,
            emptyStomach = it.emptyStomach,
            notes = it.notes
        )
    }
}

fun ResearchResult.toGlucoseResultDB(): GlucoseResultDB {
    return this.let {
        GlucoseResultDB(
            id = it.id,
            glucoseConcentration = it.glucoseConcentration,
            unit = it.unit.toGlucoseUnitTypeDB(),
            timestamp = it.timestamp,
            userId = it.userId,
            deletedOn = it.deletedOn,
            lastUpdatedOn = it.lastUpdatedOn,
            afterMedication = it.afterMedication,
            emptyStomach = it.emptyStomach,
            notes = it.notes
        )
    }
}


private fun GlucoseUnitTypeDB.toGlucoseUnitType(): GlucoseUnitType {
    return when (this) {
        GlucoseUnitTypeDB.MG_PER_DL -> GlucoseUnitType.MG_PER_DL
        GlucoseUnitTypeDB.MMOL_PER_L -> GlucoseUnitType.MMOL_PER_L
        else -> throw IllegalArgumentException("Unknown GlucoseUnitTypeDB: $this")
    }
}

private fun GlucoseUnitType.toGlucoseUnitTypeDB(): GlucoseUnitTypeDB {
    return when (this) {
        GlucoseUnitType.MG_PER_DL -> GlucoseUnitTypeDB.MG_PER_DL
        GlucoseUnitType.MMOL_PER_L -> GlucoseUnitTypeDB.MMOL_PER_L
        else -> throw IllegalArgumentException("Unknown GlucoseUnitTypeDB: $this")
    }
}

fun List<GlucoseResultDB>.toResearchResultList(): List<ResearchResult> {
    return this.map { dbResult ->
        ResearchResult(
            id = dbResult.id,
            glucoseConcentration = dbResult.glucoseConcentration,
            unit = dbResult.unit.toGlucoseUnitType(),
            timestamp = dbResult.timestamp,
            userId = dbResult.userId,
            deletedOn = dbResult.deletedOn,
            lastUpdatedOn = dbResult.lastUpdatedOn,
            afterMedication = dbResult.afterMedication,
            emptyStomach = dbResult.emptyStomach,
            notes = dbResult.notes
        )
    }
}

 fun ResearchResultCreate.toGlucoseResultDB(USER_ID: String): GlucoseResultDB {
    return this.let {
        GlucoseResultDB(
            id = UUID.randomUUID(),
            glucoseConcentration = it.glucoseConcentration,
            unit = GlucoseUnitTypeDB.valueOf(it.unit),
            timestamp = it.timestamp,
            userId = UUID.fromString(USER_ID),
            deletedOn = null,
            lastUpdatedOn = Date(),
            afterMedication = it.afterMedication,
            emptyStomach = it.emptyStomach,
            notes = it.notes,
            isSynced = false)
    }

}