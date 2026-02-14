package im.manus.atlas.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import im.manus.atlas.data.local.entity.DeliveryStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryStationDao {
    @Query("SELECT * FROM delivery_stations")
    suspend fun getAllStations(): List<DeliveryStationEntity>

    @Query("SELECT * FROM delivery_stations WHERE id = :id")
    suspend fun getStationById(id: String): DeliveryStationEntity?

    @Query("SELECT * FROM delivery_stations ORDER BY name ASC")
    fun getAllStationsFlow(): Flow<List<DeliveryStationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<DeliveryStationEntity>)

    @Query("DELETE FROM delivery_stations")
    suspend fun deleteAllStations()
}
