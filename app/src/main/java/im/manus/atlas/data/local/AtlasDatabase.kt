package im.manus.atlas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import im.manus.atlas.data.local.dao.PartnerDao
import im.manus.atlas.data.local.dao.DeliveryStationDao
import im.manus.atlas.data.local.entity.PartnerEntity
import im.manus.atlas.data.local.entity.DeliveryStationEntity

@Database(
    entities = [
        PartnerEntity::class,
        DeliveryStationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AtlasDatabase : RoomDatabase() {
    abstract fun partnerDao(): PartnerDao
    abstract fun deliveryStationDao(): DeliveryStationDao
}
