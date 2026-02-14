package im.manus.atlas.data.mapper

import im.manus.atlas.data.local.entity.DeliveryStationEntity
import im.manus.atlas.data.remote.DeliveryStationDto
import im.manus.atlas.domain.model.DeliveryStation

fun DeliveryStationDto.toEntity(): DeliveryStationEntity {
    return DeliveryStationEntity(
        id = nome,
        name = nome,
        latitude = lat,
        longitude = lon
    )
}

fun DeliveryStationEntity.toDomain(): DeliveryStation {
    return DeliveryStation(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude
    )
}

fun DeliveryStationDto.toDomain(): DeliveryStation {
    return DeliveryStation(
        id = nome,
        name = nome,
        latitude = lat,
        longitude = lon
    )
}

fun List<DeliveryStationDto>.toEntities(): List<DeliveryStationEntity> = map { it.toEntity() }
fun List<DeliveryStationEntity>.toDomain(): List<DeliveryStation> = map { it.toDomain() }
