package im.manus.atlas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.manus.atlas.data.local.entity.PartnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartnerDao {
    @Query("SELECT * FROM partners")
    suspend fun getAllPartners(): List<PartnerEntity>

    @Query("SELECT * FROM partners WHERE status = :status")
    suspend fun getPartnersByStatus(status: String): List<PartnerEntity>

    @Query("SELECT * FROM partners WHERE storeId = :storeId")
    suspend fun getPartnerById(storeId: String): PartnerEntity?

    @Query("SELECT * FROM partners ORDER BY name ASC")
    fun getAllPartnersFlow(): Flow<List<PartnerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartners(partners: List<PartnerEntity>)

    @Query("DELETE FROM partners")
    suspend fun deleteAllPartners()

    @Query("SELECT COUNT(*) FROM partners")
    suspend fun getPartnerCount(): Int
}
