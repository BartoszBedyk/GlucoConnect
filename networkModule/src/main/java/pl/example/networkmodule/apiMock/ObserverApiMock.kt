package pl.example.networkmodule.apiMock

import pl.example.networkmodule.apiData.ObserverResult
import pl.example.networkmodule.apiMethods.ObserverApiInterface
import pl.example.networkmodule.requestData.CreateObserver
import java.util.UUID

class ObserverApiMock: ObserverApiInterface {
    override suspend fun observe(createObserver: CreateObserver): UUID {
        TODO("Not yet implemented")
    }

    override suspend fun getObservedAcceptedByObserverId(observerId: String): List<ObserverResult>? {
        TODO("Not yet implemented")
    }

    override suspend fun getObservedUnAcceptedByObserverId(observerId: String): List<ObserverResult>? {
        TODO("Not yet implemented")
    }

    override suspend fun acceptObservation(createObserver: CreateObserver): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unAcceptObservation(createObserver: CreateObserver): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getObservatorByObservedIdAccepted(observedId: String): List<ObserverResult>? {
        TODO("Not yet implemented")
    }

    override suspend fun getObservatorByObservedIdUnAccepted(observedId: String): List<ObserverResult>? {
        TODO("Not yet implemented")
    }
}