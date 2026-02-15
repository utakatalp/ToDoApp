package com.todoapp.mobile.ui.register

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.todoapp.mobile.R
import com.todoapp.mobile.common.loginWithFacebook
import com.todoapp.mobile.ui.register.RegisterContract.UiAction
import com.todoapp.mobile.ui.register.RegisterContract.UiEffect
import com.todoapp.mobile.ui.register.RegisterContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDCompactOutlinedTextField
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
    uiEffect.collectWithLifecycle {
        when (it) {
            UiEffect.FacebookLogin -> {
                handleFacebookLogin(
                    context = context,
                    onAction = onAction,
                )
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        RegisterContent(
            uiState = uiState,
            onAction = onAction,
        )

        if (uiState.isRedirecting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* consume clicks */ },
                contentAlignment = Alignment.Center
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
    val verticalScroll = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            // .imePadding()
            .navigationBarsPadding()
            .background(color = TDTheme.colors.primary)
            .verticalScroll(verticalScroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
        )
        Spacer(Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(color = TDTheme.colors.background.copy(alpha = 0.25f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.logo),
                modifier = Modifier.size(40.dp),
                tint = TDTheme.colors.white
            )
        }
        TDText(text = "Create Account", style = TDTheme.typography.heading1, color = TDTheme.colors.white)
        TDText(
            modifier = Modifier.size(width = 300.dp, height = 70.dp),
            text = stringResource(R.string.join_us_and_start_organizing_your_tasks_efficiently),
            style = TDTheme.typography.heading4,
            textAlign = TextAlign.Center,
            color = TDTheme.colors.white.copy(0.8f)
        )
        Spacer(Modifier.weight(1f))
        Column(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(color = TDTheme.colors.background)
                .padding(start = 32.dp, end = 32.dp, top = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            TDCompactOutlinedTextField(
                value = uiState.fullName,
                enabled = uiState.isFullNameFieldEnabled,
                label = stringResource(R.string.full_name),
                onValueChange = { onAction(UiAction.OnFullNameChange(it)) },
                placeholder = stringResource(R.string.full_name),
                isError = false,
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_person),
                        contentDescription = stringResource(R.string.full_name),
                        tint = TDTheme.colors.onBackground.copy(0.5f)
                    )
                },
                roundedCornerShape = RoundedCornerShape(12.dp),
                height = 50.dp,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onAction(UiAction.OnFullNameFieldTap)
                }
            )
            TDCompactOutlinedTextField(
                value = uiState.email,
                enabled = uiState.isEmailFieldEnabled,
                label = stringResource(R.string.email),
                onValueChange = { onAction(UiAction.OnEmailChange(it)) },
                placeholder = stringResource(R.string.email),
                isError = uiState.emailError != null,
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_mail_white),
                        contentDescription = stringResource(R.string.email),
                        tint = when {
                            uiState.emailError != null -> TDTheme.colors.crossRed
                            else -> TDTheme.colors.onBackground.copy(alpha = 0.5f)
                        }
                    )
                },
                roundedCornerShape = RoundedCornerShape(12.dp),
                height = 50.dp,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onAction(UiAction.OnEmailFieldTap)
                }
            )
            uiState.emailError?.let {
                TDText(text = it.message, color = TDTheme.colors.red)
            }
            TDCompactOutlinedTextField(
                value = uiState.password,
                enabled = uiState.isPasswordFieldEnabled,
                label = stringResource(R.string.password),
                visualTransformation =
                    if (uiState.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                onValueChange = { onAction(UiAction.OnPasswordChange(it)) },
                placeholder = stringResource(R.string.password),
                isError = uiState.passwordError != null,
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_lock),
                        contentDescription = stringResource(R.string.password),
                        tint = when {
                            uiState.passwordError != null -> TDTheme.colors.crossRed
                            else -> TDTheme.colors.onBackground.copy(alpha = 0.5f)
                        }
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onAction(UiAction.OnPasswordVisibilityTap) }) {
                        Icon(
                            painter = painterResource(
                                if (uiState.isPasswordVisible) {
                                    R.drawable.ic_visibility_on
                                } else {
                                    R.drawable.ic_visibility_close
                                }
                            ),
                            contentDescription = stringResource(R.string.toggle_password_visibility),
                            tint = TDTheme.colors.onBackground.copy(alpha = 0.5f)
                        )
                    }
                },
                roundedCornerShape = RoundedCornerShape(12.dp),
                height = 50.dp,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onAction(UiAction.OnPasswordFieldTap)
                }
            )
            uiState.passwordError?.let {
                TDText(text = it.message, color = TDTheme.colors.red)
            }

            Spacer(modifier = Modifier.height(2.dp))

            TDPasswordStrengthIndicator(uiState = uiState)

            TDCompactOutlinedTextField(
                value = uiState.confirmPassword,
                enabled = uiState.isPasswordConfirmationFieldEnabled,
                visualTransformation =
                    if (uiState.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                label = stringResource(R.string.confirm_password),
                onValueChange = { onAction(UiAction.OnConfirmPasswordChange(it)) },
                placeholder = stringResource(R.string.confirm_password),
                isError = uiState.confirmPasswordError != null,
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_lock),
                        contentDescription = stringResource(R.string.confirm_password),
                        tint = when {
                            uiState.confirmPasswordError != null -> TDTheme.colors.crossRed
                            else -> TDTheme.colors.onBackground.copy(alpha = 0.5f)
                        }
                    )
                },
                roundedCornerShape = RoundedCornerShape(12.dp),
                height = 50.dp,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onAction(UiAction.OnConfirmPasswordFieldTap)
                }
            )
            uiState.confirmPasswordError?.let {
                TDText(text = it.message, color = TDTheme.colors.red)
            }
            uiState.generalError?.let {
                TDText(text = it.message, color = TDTheme.colors.red)
            }
            Spacer(Modifier.height(16.dp))
            TDButton(
                text = stringResource(R.string.sign_up),
                fullWidth = true,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                onAction(UiAction.OnSignUpTap)
            }
            Spacer(Modifier.height(8.dp))
            TDButton(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                text = "Sign Up with Facebook",
                fullWidth = true,
                icon = painterResource(R.drawable.ic_facebook_logo_secondary)
            ) {
                onAction(UiAction.OnFacebookSignInTap)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TDText(
                    text = stringResource(R.string.already_have_an_account),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.onBackground.copy(0.7f)
                )
                TDText(
                    text = stringResource(R.string.login),
                    color = TDTheme.colors.purple,
                    style = TDTheme.typography.heading6.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable {
                        onAction(UiAction.OnLoginTap)
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    TDText(
                        text = stringResource(R.string.by_signing_up_you_agree_to_our),
                        style = TDTheme.typography.subheading4,
                        color = TDTheme.colors.gray
                    )
                    TDText(
                        text = stringResource(R.string.terms_of_service),
                        style = TDTheme.typography.subheading4.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = TDTheme.colors.gray,
                        modifier = Modifier.clickable {
                            onAction(UiAction.OnTermsOfServiceTap)
                        }
                    )
                }
                Row {
                    TDText(
                        text = stringResource(R.string.and),
                        style = TDTheme.typography.subheading4,
                        color = TDTheme.colors.gray
                    )
                    TDText(
                        text = stringResource(R.string.privacy_policy),
                        style = TDTheme.typography.subheading4.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = TDTheme.colors.gray,
                        modifier = Modifier.clickable {
                            onAction(UiAction.OnPrivacyPolicyTap)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Handles Facebook login with context safety and error reporting.
 */
private suspend fun handleFacebookLogin(
    context: Context,
    onAction: (UiAction) -> Unit,
) {
    val activity = context as? FragmentActivity
        ?: run {
            onAction(
                UiAction.OnFacebookLoginFail(
                    IllegalStateException("Facebook login requires a FragmentActivity context")
                )
            )
            return
        }

    loginWithFacebook(activity = activity)
        .onSuccess { token ->
            Log.d("token", token)
            onAction(UiAction.OnSuccessfulFacebookLogin(token))
        }
        .onFailure { throwable ->
            onAction(UiAction.OnFacebookLoginFail(throwable))
            Log.d("token", "fail")
        }
}

@Preview(showBackground = true)
@Composable
private fun RegisterContentPreview() {
    TDTheme {
        RegisterContent(
            uiState = UiState(
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
            uiState = UiState(
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
