package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.ResearchResult
import pl.example.networkmodule.requestData.ResearchResultCreate
import pl.example.networkmodule.requestData.ResearchResultUpdate

interface ResultApiInterface {
    suspend fun getResearchResultsById(id: String): ResearchResult?
    suspend fun getAllResearchResults(): List<ResearchResult>?
    suspend fun getThreeResultsById(id: String): List<ResearchResult>?
    suspend fun updateResearchResult(updateForm: ResearchResultUpdate): Boolean
    suspend fun createResearchResult(createForm: ResearchResultCreate): String?
    suspend fun syncResult(researchResult: ResearchResult): Boolean
    suspend fun deleteResearchResult(id: String): Boolean
    suspend fun safeDeleteResearchResult(id: String): Boolean
    suspend fun getResultsByUserId(id: String): List<ResearchResult>?
}