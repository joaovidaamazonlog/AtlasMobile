package im.manus.atlas.data.repository

import im.manus.atlas.data.local.dao.PartnerDao
import im.manus.atlas.data.local.dao.DeliveryStationDao
import im.manus.atlas.data.mapper.toEntity
import im.manus.atlas.data.mapper.toDomain
import im.manus.atlas.data.remote.AtlasApi
import im.manus.atlas.domain.model.Partner
import im.manus.atlas.domain.model.DeliveryStation
import im.manus.atlas.domain.repository.AtlasRepository
import im.manus.atlas.domain.repository.GeoJsonFeature
import im.manus.atlas.domain.repository.GeoJsonGeometry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class AtlasRepositoryImpl @Inject constructor(
    private val atlasApi: AtlasApi,
    private val partnerDao: PartnerDao,
    private val deliveryStationDao: DeliveryStationDao
) : AtlasRepository {

    override suspend fun getPartners(): Result<List<Partner>> = try {
        val response = atlasApi.getPartnersData()
        val partners = response.allMarkerData.map { it.toDomain() }

        partnerDao.deleteAllPartners()
        partnerDao.insertPartners(response.allMarkerData.map { it.toEntity() })

        Result.success(partners)
    } catch (e: Exception) {
        Timber.e(e, "Error fetching partners")
        val cachedPartners = partnerDao.getAllPartners().map { it.toDomain() }
        if (cachedPartners.isNotEmpty()) {
            Result.success(cachedPartners)
        } else {
            Result.failure(e)
        }
    }

    override suspend fun getDeliveryStations(): Result<List<DeliveryStation>> = try {
        val response = atlasApi.getPartnersData()
        val stations = response.deliveryStations.map { it.toDomain() }

        deliveryStationDao.deleteAllStations()
        deliveryStationDao.insertStations(response.deliveryStations.map { it.toEntity() })

        Result.success(stations)
    } catch (e: Exception) {
        Timber.e(e, "Error fetching delivery stations")
        val cachedStations = deliveryStationDao.getAllStations().map { it.toDomain() }
        if (cachedStations.isNotEmpty()) {
            Result.success(cachedStations)
        } else {
            Result.failure(e)
        }
    }

    override suspend fun getGeoJsonFeatures(): Flow<GeoJsonFeature> {
        return partnerDao.getAllPartnersFlow().map { partners ->
            partners.map { partner ->
                GeoJsonFeature(
                    id = partner.storeId,
                    type = "Feature",
                    geometry = GeoJsonGeometry(
                        type = "Point",
                        coordinates = listOf(partner.longitude, partner.latitude)
                    ),
                    properties = mapOf(
                        "name" to partner.name,
                        "status" to partner.status,
                        "capacity" to partner.capacity
                    )
                )
            }
        }
    }

    override suspend fun filterPartnersByStatus(status: String): Result<List<Partner>> = try {
        val partners = partnerDao.getPartnersByStatus(status).map { it.toDomain() }
        Result.success(partners)
    } catch (e: Exception) {
        Timber.e(e, "Error filtering partners by status")
        Result.failure(e)
    }
}
