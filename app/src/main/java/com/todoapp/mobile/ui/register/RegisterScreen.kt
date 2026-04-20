package com.todoapp.mobile.ui.register

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.todoapp.mobile.R
import com.todoapp.mobile.common.loginWithFacebook
import com.todoapp.mobile.data.auth.GoogleSignInManager
import com.todoapp.mobile.ui.register.RegisterContract.UiAction
import com.todoapp.mobile.ui.register.RegisterContract.UiEffect
import com.todoapp.mobile.ui.register.RegisterContract.UiState
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun RegisterScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current
    uiEffect.collectWithLifecycle(minActiveState = Lifecycle.State.CREATED) {
        when (it) {
            UiEffect.FacebookLogin -> {
                handleFacebookLogin(context = context, onAction = onAction)
            }
            UiEffect.LaunchGoogleSignIn -> {
                GoogleSignInManager
                    .getGoogleIdToken(context)
                    .onSuccess { token -> onAction(UiAction.OnGoogleSignInResult(token)) }
                    .onFailure { error ->
                        onAction(UiAction.OnGoogleSignInFailed(error.message ?: "Sign-in cancelled"))
                    }
            }
            is UiEffect.ShowToast -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RegisterContent(uiState = uiState, onAction = onAction)

        if (uiState.isRedirecting) {
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { },
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun RegisterContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) {
        RegisterPortraitContent(uiState = uiState, onAction = onAction)
    } else {
        RegisterLandscapeContent(uiState = uiState, onAction = onAction)
    }
}

@Composable
private fun RegisterPortraitContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = TDTheme.colors.pendingGray)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Spacer(Modifier.height(32.dp))
        Box(
            modifier =
            Modifier
                .size(70.dp)
                .background(color = TDTheme.colors.background.copy(alpha = 0.25f), shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.logo),
                modifier = Modifier.size(40.dp),
                tint = TDTheme.colors.white,
            )
        }
        TDText(
            text = stringResource(R.string.create_account),
            style = TDTheme.typography.heading1,
            color = TDTheme.colors.white,
        )
        TDText(
            modifier = Modifier.size(width = 300.dp, height = 70.dp),
            text = stringResource(R.string.join_us_and_start_organizing_your_tasks_efficiently),
            style = TDTheme.typography.heading4,
            textAlign = TextAlign.Center,
            color = TDTheme.colors.white.copy(0.8f),
        )
        Spacer(Modifier.weight(1f))
        Column(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(color = TDTheme.colors.background)
                .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 16.dp),
        ) {
            RegisterFormPanel(uiState = uiState, onAction = onAction)
        }
    }
}

@Composable
private fun RegisterLandscapeContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Row(Modifier.fillMaxSize()) {
        RegisterBrandingPanel(
            modifier =
            Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        Column(
            modifier =
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(TDTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 16.dp),
        ) {
            RegisterFormPanel(uiState = uiState, onAction = onAction)
        }
    }
}

suspend fun handleFacebookLogin(
    context: Context,
    onAction: (UiAction) -> Unit,
) {
    val activity =
        context as? FragmentActivity
            ?: run {
                onAction(
                    UiAction.OnFacebookLoginFail(
                        IllegalStateException("Facebook login requires a FragmentActivity context"),
                    ),
                )
                return
            }

    loginWithFacebook(activity = activity)
        .onSuccess { token ->
            Log.d("token", token)
            onAction(UiAction.OnSuccessfulFacebookLogin(token))
        }.onFailure { throwable ->
            onAction(UiAction.OnFacebookLoginFail(throwable))
            Log.d("token", "fail")
        }
}

@Preview(showBackground = true)
@Composable
private fun RegisterContentPreview() {
    TDTheme {
        RegisterContent(
            uiState =
            UiState(
                fullName = "",
                email = "natalia@example.com",
                password = "password",
                confirmPassword = "password",
                isPasswordVisible = true,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RegisterContentDarkPreview() {
    TDTheme {
        RegisterContent(
            uiState =
            UiState(
                fullName = "Natalia Smith",
                email = "natalia@example.com",
                password = "password123",
                confirmPassword = "password123",
                isPasswordVisible = false,
            ),
            onAction = {},
        )
    }
}
