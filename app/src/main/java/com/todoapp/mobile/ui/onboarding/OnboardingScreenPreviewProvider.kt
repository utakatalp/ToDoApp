package com.todoapp.mobile.ui.onboarding

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class OnboardingScreenPreviewProvider : PreviewParameterProvider<OnboardingContract.UiState> {
    override val values: Sequence<OnboardingContract.UiState>
        get() =
            sequenceOf(
                OnboardingContract.UiState(
                    bgIndex = 0,
                ),
                OnboardingContract.UiState(
                    bgIndex = 1,
                ),
                OnboardingContract.UiState(
                    bgIndex = 2,
                ),
                OnboardingContract.UiState(
                    bgIndex = 3,
                ),
            )
}
