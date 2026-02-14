package im.manus.atlas.domain.usecase

import im.manus.atlas.domain.model.DeliveryStation
import im.manus.atlas.domain.repository.AtlasRepository
import javax.inject.Inject

class GetDeliveryStationsUseCase @Inject constructor(
    private val repository: AtlasRepository
) {
    suspend operator fun invoke(): Result<List<DeliveryStation>> {
        return repository.getDeliveryStations()
    }
}
