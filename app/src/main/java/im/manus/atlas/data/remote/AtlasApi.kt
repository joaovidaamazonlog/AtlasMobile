package im.manus.atlas.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface AtlasApi {
    @GET("dados_mapa.json")
    suspend fun getPartnersData(): PartnerDataResponse

    @GET("optimization_data.geojson")
    @Streaming
    suspend fun getOptimizationGeoJson(): ResponseBody
}

data class PartnerDataResponse(
    val allMarkerData: List<PartnerDto>,
    val deliveryStations: List<DeliveryStationDto>
)

data class PartnerDto(
    val store_id: String?,
    val partner_name: String,
    val status: String,
    val delivery_station: String,
    val lat: Double,
    val lon: Double,
    val radius: Int,
    val capacity: Int,
    val prospecting_difficulty: String?,
    val cluster: String?,
    val decision: String?,
    val telefone: String?,
    val salesforce_id: String?,
    val jurisdiction_type: String?,
    val city: String?
)

data class DeliveryStationDto(
    val nome: String,
    val lat: Double,
    val lon: Double
)
