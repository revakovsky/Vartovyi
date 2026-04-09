package com.revakovskyi.vartovyi.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.usecase.onboarding.SetOnboardingCompletedUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiContract.State())
    val state: StateFlow<OnboardingUiContract.State> = _state.asStateFlow()

    private val _events = Channel<OnboardingUiContract.Event>(Channel.BUFFERED)
    val events: Flow<OnboardingUiContract.Event> = _events.receiveAsFlow()

    fun onAction(action: OnboardingUiContract.Action) {
        when (action) {
            is OnboardingUiContract.Action.NextPage -> nextPage()
            is OnboardingUiContract.Action.PreviousPage -> previousPage()
            is OnboardingUiContract.Action.PageChanged -> onPageChanged(action.pageIndex)
            is OnboardingUiContract.Action.Complete -> complete()
            is OnboardingUiContract.Action.Skip -> skip()
            is OnboardingUiContract.Action.ShowManually -> showManually()
        }
    }

    private fun nextPage() {
        val currentPage = _state.value.currentPage
        val totalPages = _state.value.totalPages
        if (currentPage < totalPages - 1) {
            _state.update { it.copy(currentPage = currentPage + 1) }
        }
    }

    private fun previousPage() {
        val currentPage = _state.value.currentPage
        if (currentPage > 0) {
            _state.update { it.copy(currentPage = currentPage - 1) }
        }
    }

    private fun onPageChanged(pageIndex: Int) {
        _state.update { it.copy(currentPage = pageIndex) }
    }

    private fun complete() {
        viewModelScope.launch {
            setOnboardingCompletedUseCase()
            _events.send(OnboardingUiContract.Event.Close)
        }
    }

    private fun skip() {
        viewModelScope.launch {
            _events.send(OnboardingUiContract.Event.Close)
        }
    }

    private fun showManually() {
        _state.update { it.copy(currentPage = 0, canSkip = true) }
    }

}
