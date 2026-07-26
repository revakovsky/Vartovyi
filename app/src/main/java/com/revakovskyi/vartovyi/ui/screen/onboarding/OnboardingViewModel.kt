package com.revakovskyi.vartovyi.ui.screen.onboarding

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.revakovskyi.vartovyi.model.OnboardingPage
import com.revakovskyi.vartovyi.navigation.Routes
import com.revakovskyi.vartovyi.usecase.onboarding.ObserveOnboardingCompletedUseCase
import com.revakovskyi.vartovyi.usecase.onboarding.SetOnboardingCompletedUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val ONBOARDING_VIEW_MODEL_TAG = "OnboardingViewModel"

class OnboardingViewModel(
    savedStateHandle: SavedStateHandle,
    private val observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
) : ViewModel() {

    private val startPage = savedStateHandle.toRoute<Routes.Onboarding>().startPage
        .coerceIn(0, OnboardingPage.entries.lastIndex)

    private val _state = MutableStateFlow(OnboardingUiContract.State(currentPage = startPage))
    val state: StateFlow<OnboardingUiContract.State> = _state.asStateFlow()

    private val _events = Channel<OnboardingUiContract.Event>(Channel.BUFFERED)
    val events: Flow<OnboardingUiContract.Event> = _events.receiveAsFlow()

    init {
        observeCompleted()
    }

    fun onAction(action: OnboardingUiContract.Action) {
        when (action) {
            is OnboardingUiContract.Action.NextPage -> nextPage()
            is OnboardingUiContract.Action.PreviousPage -> previousPage()
            is OnboardingUiContract.Action.PageChanged -> onPageChanged(action.pageIndex)
            is OnboardingUiContract.Action.Complete -> complete()
            is OnboardingUiContract.Action.Skip -> skip()
        }
    }

    private fun observeCompleted() {
        viewModelScope.launch {
            observeOnboardingCompletedUseCase().collect { isCompleted ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isCompleted = isCompleted,
                    )
                }
            }
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
            runCatching { setOnboardingCompletedUseCase() }
                .onFailure { throwable ->
                    Log.e(
                        ONBOARDING_VIEW_MODEL_TAG,
                        "Failed to mark onboarding completed",
                        throwable,
                    )
                }

            _events.send(OnboardingUiContract.Event.Close)
        }
    }

    private fun skip() {
        viewModelScope.launch {
            val wasCompletedBefore = _state.value.isCompleted

            runCatching { setOnboardingCompletedUseCase() }
                .onFailure { throwable ->
                    Log.e(
                        ONBOARDING_VIEW_MODEL_TAG,
                        "Failed to mark onboarding completed",
                        throwable,
                    )
                }

            if (!wasCompletedBefore) {
                _events.send(OnboardingUiContract.Event.ShowSkipHint)
            }
            _events.send(OnboardingUiContract.Event.Close)
        }
    }

}
