package im.manus.atlas.data.mapper

import im.manus.atlas.data.local.entity.PartnerEntity
import im.manus.atlas.data.remote.PartnerDto
import im.manus.atlas.domain.model.Partner

fun PartnerDto.toEntity(): PartnerEntity {
    return PartnerEntity(
        storeId = store_id ?: "unknown",
        name = partner_name,
        status = status,
        deliveryStation = delivery_station,
        latitude = lat,
        longitude = lon,
        radius = radius,
        capacity = capacity,
        prospectingDifficulty = prospecting_difficulty,
        clusterName = cluster,
        decision = decision,
        telefone = telefone,
        salesforceId = salesforce_id,
        jurisdictionType = jurisdiction_type,
        city = city
    )
}

fun PartnerEntity.toDomain(): Partner {
    return Partner(
        storeId = storeId,
        name = name,
        status = status,
        deliveryStation = deliveryStation,
        lat = latitude,
        lon = longitude,
        radius = radius,
        capacity = capacity,
        prospectingDifficulty = prospectingDifficulty ?: "Baixa",
        clusterName = clusterName ?: "N/A",
        decision = decision ?: "",
        telefone = telefone ?: "",
        salesforceId = salesforceId ?: "",
        jurisdictionType = jurisdictionType ?: "",
        city = city ?: ""
    )
}

fun PartnerDto.toDomain(): Partner {
    return Partner(
        storeId = store_id,
        name = partner_name,
        status = status,
        deliveryStation = delivery_station,
        lat = lat,
        lon = lon,
        radius = radius,
        capacity = capacity,
        prospectingDifficulty = prospecting_difficulty ?: "Baixa",
        clusterName = cluster ?: "N/A",
        decision = decision ?: "",
        telefone = telefone ?: "",
        salesforceId = salesforce_id ?: "",
        jurisdictionType = jurisdiction_type ?: "",
        city = city ?: ""
    )
}

fun List<PartnerDto>.toEntities(): List<PartnerEntity> = map { it.toEntity() }
fun List<PartnerEntity>.toDomain(): List<Partner> = map { it.toDomain() }
