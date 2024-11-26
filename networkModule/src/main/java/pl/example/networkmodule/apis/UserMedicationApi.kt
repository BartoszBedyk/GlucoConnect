package pl.example.networkmodule.apis

import pl.example.networkmodule.KtorClient

class UserMedicationApi(private val ktorClient: KtorClient) {
    private val client = ktorClient.client
}