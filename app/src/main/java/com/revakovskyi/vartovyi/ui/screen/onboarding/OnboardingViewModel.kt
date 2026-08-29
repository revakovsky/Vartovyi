package com.revakovskyi.vartovyi.ui.screen.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.contract.CrashReporter
import com.revakovskyi.vartovyi.model.OnboardingPage
import com.revakovskyi.vartovyi.usecase.keywords.ApplyCityToSeededKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.onboarding.ObserveOnboardingCompletedUseCase
import com.revakovskyi.vartovyi.usecase.onboarding.SetOnboardingCityUseCase
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
    private val startPage: Int,
    private val observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val setOnboardingCityUseCase: SetOnboardingCityUseCase,
    private val applyCityToSeededKeywordsUseCase: ApplyCityToSeededKeywordsUseCase,
    private val crashReporter: CrashReporter,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiContract.State())
    val state: StateFlow<OnboardingUiContract.State> = _state.asStateFlow()

    private val _events = Channel<OnboardingUiContract.Event>(Channel.BUFFERED)
    val events: Flow<OnboardingUiContract.Event> = _events.receiveAsFlow()

    private var isSessionPagesFrozen = false

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
            is OnboardingUiContract.Action.UpdateCityInput -> updateCityInput(action.value)
            is OnboardingUiContract.Action.SubmitCity -> submitCity()
            is OnboardingUiContract.Action.SkipCity -> nextPage()
        }
    }

    private fun observeCompleted() {
        viewModelScope.launch {
            observeOnboardingCompletedUseCase().collect { isCompleted ->
                if (!isSessionPagesFrozen) {
                    freezeSessionPages(isCompleted)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        isCompleted = isCompleted,
                    )
                }
            }
        }
    }

    /**
     * Runs once per session, so a later `complete()`/`skip()` can't make the city page vanish
     * mid-flow. Also converts [startPage] from an index in the full enum to an index in this
     * session's (possibly shorter) page list
     */
    private fun freezeSessionPages(isCompleted: Boolean) {
        isSessionPagesFrozen = true

        val sessionPages = if (isCompleted) {
            OnboardingPage.entries.filter { page -> page != OnboardingPage.CITY }
        } else {
            OnboardingPage.entries
        }

        val requestedPage = OnboardingPage.entries.getOrNull(startPage)
        val safeStartPage = sessionPages.indexOf(requestedPage).coerceAtLeast(0)

        _state.update { it.copy(pages = sessionPages, currentPage = safeStartPage) }
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

    private fun updateCityInput(value: String) {
        _state.update { it.copy(cityInput = value) }
    }

    /**
     * Order matters: [applyCityToSeededKeywordsUseCase] must read the old city before
     * [setOnboardingCityUseCase] overwrites it. A failed write still lets the user move on
     */
    private fun submitCity() {
        val city = _state.value.cityInput

        viewModelScope.launch {
            runCatching {
                applyCityToSeededKeywordsUseCase(city)
                setOnboardingCityUseCase(city)
            }.onFailure { throwable -> crashReporter.report(throwable) }

            nextPage()
        }
    }

}
