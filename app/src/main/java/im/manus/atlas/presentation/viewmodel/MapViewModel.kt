package im.manus.atlas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import im.manus.atlas.domain.model.Partner
import im.manus.atlas.domain.usecase.GetPartnersUseCase
import im.manus.atlas.domain.usecase.GetDeliveryStationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getPartnersUseCase: GetPartnersUseCase,
    private val getDeliveryStationsUseCase: GetDeliveryStationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            try {
                val result = getPartnersUseCase()
                result.fold(
                    onSuccess = { partners ->
                        _uiState.value = MapUiState.Success(partners)
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "Error loading partners")
                        _uiState.value = MapUiState.Error(
                            exception.message ?: "Erro desconhecido ao carregar dados"
                        )
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in loadData")
                _uiState.value = MapUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun refreshData() {
        loadData()
    }
}

sealed class MapUiState {
    object Loading : MapUiState()
    data class Success(val partners: List<Partner>) : MapUiState()
    data class Error(val message: String) : MapUiState()
}
