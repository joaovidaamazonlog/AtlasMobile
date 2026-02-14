package im.manus.atlas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partners")
data class PartnerEntity(
    @PrimaryKey
    val storeId: String,
    val name: String,
    val status: String,
    val deliveryStation: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int,
    val capacity: Int,
    val prospectingDifficulty: String? = null,
    val clusterName: String? = null,
    val decision: String? = null,
    val telefone: String? = null,
    val salesforceId: String? = null,
    val jurisdictionType: String? = null,
    val city: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
