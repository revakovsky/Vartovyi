package com.revakovskyi.vartovyi.ui.screen.legal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.constants.PRIVACY_POLICY_URL
import com.revakovskyi.vartovyi.constants.TERMS_OF_USE_URL
import com.revakovskyi.vartovyi.usecase.legal.AcceptCurrentLegalDocumentsUseCase
import com.revakovskyi.vartovyi.usecase.legal.ObserveLegalConsentStateUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LegalConsentViewModel(
    observeLegalConsentStateUseCase: ObserveLegalConsentStateUseCase,
    private val acceptCurrentLegalDocumentsUseCase: AcceptCurrentLegalDocumentsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LegalConsentUiContract.State())
    val state: StateFlow<LegalConsentUiContract.State> = _state.asStateFlow()

    private val _events = Channel<LegalConsentUiContract.Event>(Channel.BUFFERED)
    val events: Flow<LegalConsentUiContract.Event> = _events.receiveAsFlow()

    init {
        observeLegalConsentStateUseCase()
            .onEach { isAccepted ->
                if (!isAccepted) delay(1000)

                _state.update {
                    it.copy(
                        isLoading = false,
                        isAccepted = isAccepted,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: LegalConsentUiContract.Action) {
        when (action) {
            is LegalConsentUiContract.Action.OpenPrivacyPolicy -> openPrivacyPolicy()
            is LegalConsentUiContract.Action.OpenTermsOfUse -> openTermsOfUse()
            is LegalConsentUiContract.Action.Confirm -> confirm()
            is LegalConsentUiContract.Action.Refuse -> refuse()
        }
    }

    private fun openPrivacyPolicy() {
        emitOpenUrl(url = PRIVACY_POLICY_URL)
    }

    private fun openTermsOfUse() {
        emitOpenUrl(url = TERMS_OF_USE_URL)
    }

    private fun emitOpenUrl(url: String) {
        viewModelScope.launch {
            _events.send(LegalConsentUiContract.Event.OpenUrl(url = url))
        }
    }

    private fun confirm() {
        viewModelScope.launch {
            acceptCurrentLegalDocumentsUseCase()
        }
    }

    private fun refuse() {
        viewModelScope.launch {
            _events.send(LegalConsentUiContract.Event.CloseApplication)
        }
    }

}
