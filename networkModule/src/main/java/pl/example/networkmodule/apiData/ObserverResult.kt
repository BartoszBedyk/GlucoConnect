package pl.example.networkmodule.apiData

import kotlinx.serialization.Serializable
import pl.example.networkmodule.serializers.UUIDSerializer
import java.util.UUID
@Serializable
data class ObserverResult(
    @Serializable(with = UUIDSerializer::class)
     val id : UUID,
    @Serializable(with = UUIDSerializer::class)
     val observerId: UUID,
    @Serializable(with = UUIDSerializer::class)
     val observedId: UUID,
     val isAccepted: Boolean,
)