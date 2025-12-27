package com.todoapp.mobile.ui.onboarding

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiAction
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun OnboardingScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(TDTheme.colors.purple)
                    .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.weight(0.30f))

            Image(
                modifier =
                    Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.logo_text),
                contentDescription = null,
            )

            Spacer(Modifier.weight(0.30f))

            val bgImages =
                rememberSaveable {
                    listOf(
                        R.drawable.onboarding_bg,
                        R.drawable.onboarding_bg1,
                        R.drawable.onboarding_bg2,
                        R.drawable.onboarding_bg3,
                    )
                }

            Crossfade(
                modifier =
                    Modifier
                        .heightIn(200.dp)
                        .weight(2f),
                targetState = uiState.bgIndex,
            ) { idx ->
                Image(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    painter = painterResource(id = bgImages[idx]),
                    contentDescription = null,
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(TDTheme.colors.background)
                        .navigationBarsPadding(),

                ) {
                TDText(
                    modifier =
                        Modifier
                            .padding(top = 36.dp, start = 24.dp),
                    textAlign = TextAlign.Start,
                    color = TDTheme.colors.onBackground,
                    style = TDTheme.typography.heading1,
                    text = stringResource(id = R.string.onboarding_title),
                )

                Spacer(modifier = Modifier.height(12.dp))

                TDText(
                    modifier =
                        Modifier
                            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp, top = 4.dp),
                    textAlign = TextAlign.Start,
                    color = TDTheme.colors.onBackground,
                    style = TDTheme.typography.regularTextStyle,
                    text = stringResource(id = R.string.onboarding_description),
                )

                TDButton(
                    modifier =
                        Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .padding(horizontal = 12.dp),
                    text = stringResource(id = R.string.onboarding_get_started),
                    isEnable = true,
                    type = TDButtonType.PRIMARY,
                    size = TDButtonSize.MEDIUM,
                    icon = null,
                    onClick = {
                        onAction(UiAction.OnRegisterClick)
                    },
                )

                TDText(
                    modifier =
                        Modifier
                            .clickable { onAction(UiAction.OnLoginClick) }
                            .padding(bottom = 12.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                    fullText = stringResource(id = R.string.onboarding_login_span),
                    spanText = stringResource(id = R.string.onboarding_login_text_span),
                    style = TDTheme.typography.regularTextStyle.copy(
                        color = TDTheme.colors.onBackground
                    ),
                    spanStyle =
                        SpanStyle(
                            color = TDTheme.colors.purple,
                            fontWeight = FontWeight.Bold,
                        ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {
        Row(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .background(TDTheme.colors.purple)
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier =
                        Modifier
                            .size(150.dp),
                    painter = painterResource(id = R.drawable.logo_text),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.height(16.dp))

                val bgImages =
                    rememberSaveable {
                        listOf(
                            R.drawable.onboarding_bg,
                            R.drawable.onboarding_bg1,
                            R.drawable.onboarding_bg2,
                            R.drawable.onboarding_bg3,
                        )
                    }

                Crossfade(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    targetState = uiState.bgIndex,
                ) { idx ->
                    Box {
                        Image(
                            modifier =
                                Modifier
                                    .fillMaxWidth(1f)
                                    .heightIn(max = 260.dp)
                                    .aspectRatio(1.3f),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            painter = painterResource(id = bgImages[idx]),
                            contentDescription = null,
                        )
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .background(
                            TDTheme.colors.background
                        )
                        .weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                TDText(
                    modifier =
                        Modifier
                            .padding(top = 48.dp, start = 24.dp),
                    textAlign = TextAlign.Start,
                    color = TDTheme.colors.onBackground,
                    style = TDTheme.typography.heading1,
                    text = stringResource(id = R.string.onboarding_title),
                )

                Spacer(modifier = Modifier.height(12.dp))

                TDText(
                    modifier =
                        Modifier
                            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
                    textAlign = TextAlign.Start,
                    color = TDTheme.colors.onBackground,
                    style = TDTheme.typography.regularTextStyle,
                    text = stringResource(id = R.string.onboarding_description),
                )

                TDButton(
                    modifier =
                        Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .padding(horizontal = 32.dp),
                    text = stringResource(id = R.string.onboarding_get_started),
                    isEnable = true,
                    type = TDButtonType.PRIMARY,
                    size = TDButtonSize.MEDIUM,
                    icon = null,
                    onClick = {
                        onAction(UiAction.OnRegisterClick)
                    },
                )

                TDText(
                    modifier =
                        Modifier
                            .clickable { onAction(UiAction.OnLoginClick) }
                            .padding(bottom = 12.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                    fullText = stringResource(id = R.string.onboarding_login_span),
                    spanText = stringResource(id = R.string.onboarding_login_text_span),
                    style = TDTheme.typography.regularTextStyle.copy(
                        color = TDTheme.colors.onBackground
                    ),
                    spanStyle =
                        SpanStyle(
                            color = TDTheme.colors.purple,
                            fontWeight = FontWeight.Bold,
                        ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 830,
    heightDp = 400,
)
@Composable
fun OnboardingScreenLandScapePreview(
    @PreviewParameter(OnboardingScreenPreviewProvider::class) uiState: UiState,
) {
    TDTheme {
        OnboardingScreen(
            uiState = uiState,
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview(
    @PreviewParameter(OnboardingScreenPreviewProvider::class) uiState: UiState,
) {
    TDTheme {
        OnboardingScreen(
            uiState = uiState,
            onAction = {},
        )
    }
}
