package im.manus.atlas.domain.usecase

import im.manus.atlas.domain.model.Partner
import im.manus.atlas.domain.repository.AtlasRepository
import javax.inject.Inject

class GetPartnersUseCase @Inject constructor(
    private val repository: AtlasRepository
) {
    suspend operator fun invoke(): Result<List<Partner>> {
        return repository.getPartners()
    }
}
