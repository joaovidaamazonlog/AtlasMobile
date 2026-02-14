package im.manus.atlas.domain.repository

import im.manus.atlas.domain.model.Partner
import im.manus.atlas.domain.model.DeliveryStation
import kotlinx.coroutines.flow.Flow

interface AtlasRepository {
    suspend fun getPartners(): Result<List<Partner>>
    suspend fun getDeliveryStations(): Result<List<DeliveryStation>>
    suspend fun getGeoJsonFeatures(): Flow<GeoJsonFeature>
    suspend fun filterPartnersByStatus(status: String): Result<List<Partner>>
}

data class GeoJsonFeature(
    val id: String,
    val type: String,
    val geometry: GeoJsonGeometry,
    val properties: Map<String, Any?>
)

data class GeoJsonGeometry(
    val type: String,
    val coordinates: List<Double>
)
