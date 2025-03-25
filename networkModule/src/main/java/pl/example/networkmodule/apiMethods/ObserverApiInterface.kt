package pl.example.networkmodule.apiMethods

import pl.example.networkmodule.apiData.ObserverResult
import pl.example.networkmodule.requestData.CreateObserver
import java.util.UUID

interface ObserverApiInterface {
    suspend fun observe(createObserver: CreateObserver): UUID?
    suspend fun getObservedAcceptedByObserverId(observerId: String): List<ObserverResult>?
    suspend fun getObservedUnAcceptedByObserverId(observerId: String): List<ObserverResult>?
    suspend fun acceptObservation(createObserver: CreateObserver): Boolean
    suspend fun unAcceptObservation(createObserver: CreateObserver): Boolean
    suspend fun getObservatorByObservedIdAccepted(observedId: String): List<ObserverResult>?
    suspend fun getObservatorByObservedIdUnAccepted(observedId: String): List<ObserverResult>?
}