package com.todoapp.mobile.ui.login

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.login.LoginContract.UiAction
import com.todoapp.mobile.ui.login.LoginContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun LoginScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    LoginContent(
        uiState = uiState,
        onAction = onAction,
    )
}

@Composable
private fun LoginContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val verticalScroll = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .imePadding()
            .background(color = TDTheme.colors.primary)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    color = TDTheme.colors.background.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.logo),
                modifier = Modifier.size(40.dp),
                tint = TDTheme.colors.white
            )
        }
        Spacer(Modifier.height(12.dp))
        TDText(
            text = stringResource(R.string.login_header),
            style = TDTheme.typography.heading1,
            color = TDTheme.colors.white
        )
        TDText(
            text = stringResource(R.string.elevate_your_productivity),
            style = TDTheme.typography.heading4,
            color = TDTheme.colors.white.copy(0.8f)
        )
        Spacer(Modifier.height(24.dp))

        Column(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(color = TDTheme.colors.background)
                .padding(start = 32.dp, end = 32.dp, top = 32.dp)
                .verticalScroll(verticalScroll)
        ) {
            TDText(
                text = stringResource(R.string.welcome_back),
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )
            Spacer(Modifier.height(4.dp))
            TDText(
                text = stringResource(R.string.please_sign_in_to_your_account),
                style = TDTheme.typography.heading5,
                color = TDTheme.colors.gray
            )
            Spacer(Modifier.height(24.dp))

            TDCompactOutlinedTextField(
                value = uiState.email,
                enabled = uiState.isEmailFieldEnabled,
                label = stringResource(R.string.email_address),
                onValueChange = { onAction(UiAction.OnEmailChange(it)) },
                placeholder = stringResource(R.string.email),
                isError = !uiState.emailError.isNullOrBlank(),
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_mail_white),
                        contentDescription = stringResource(R.string.email),
                        tint = TDTheme.colors.onBackground.copy(0.5f)
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
            uiState.emailError
                ?.takeIf { it.isNotBlank() }
                ?.let { errorText ->
                    TDText(text = errorText, color = TDTheme.colors.red)
                }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TDText(
                    text = stringResource(R.string.password),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.onBackground
                )
                TDText(
                    text = stringResource(R.string.forgot_password),
                    style = TDTheme.typography.subheading4,
                    color = TDTheme.colors.primary,
                    modifier = Modifier.clickable {
                        onAction(UiAction.OnForgotPasswordTap)
                    }
                )
            }
            Spacer(Modifier.height(4.dp))

            TDCompactOutlinedTextField(
                value = uiState.password,
                enabled = uiState.isPasswordFieldEnabled,
                label = null,
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                onValueChange = { onAction(UiAction.OnPasswordChange(it)) },
                placeholder = stringResource(R.string.password),
                isError = !uiState.passwordError.isNullOrBlank(),
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_lock),
                        contentDescription = stringResource(R.string.password),
                        tint = TDTheme.colors.onBackground.copy(0.5f)
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
                            tint = TDTheme.colors.onBackground.copy(0.5f)

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
            uiState.passwordError
                ?.takeIf { it.isNotBlank() }
                ?.let { errorText ->
                    TDText(text = errorText, color = TDTheme.colors.red)
                }

            Spacer(Modifier.height(24.dp))

            TDButton(
                text = stringResource(R.string.login),
                fullWidth = true,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                onAction(UiAction.OnLoginTap)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = TDTheme.colors.gray.copy(0.3f)
                )
                TDText(
                    text = stringResource(R.string.or_continue_with),
                    style = TDTheme.typography.subheading4,
                    color = TDTheme.colors.gray,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = TDTheme.colors.gray.copy(0.3f)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TDButton(
                    text = stringResource(R.string.google),
                    fullWidth = false,
                    type = TDButtonType.OUTLINE,
                    icon = painterResource(R.drawable.ic_google_logo),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    onAction(UiAction.OnGoogleSignInTap)
                }
                Spacer(Modifier.width(12.dp))
                TDButton(
                    text = stringResource(R.string.facebook),
                    fullWidth = false,
                    type = TDButtonType.OUTLINE,
                    icon = painterResource(R.drawable.ic_facebook_logo),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    onAction(UiAction.OnFacebookSignInTap)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TDText(
                    text = stringResource(R.string.dont_have_an_account),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.onBackground.copy(0.7f)
                )
                TDText(
                    text = stringResource(R.string.register),
                    color = TDTheme.colors.primary,
                    style = TDTheme.typography.heading6.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable {
                        onAction(UiAction.OnRegisterTap)
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

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

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    TDTheme {
        LoginContent(
            uiState = UiState(
                email = "name@example.com",
                password = "ExamplePassword123",
                isPasswordVisible = true
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginContentDarkPreview() {
    TDTheme {
        LoginContent(
            uiState = UiState(
                email = "name@example.com",
                password = "ExamplePassword123",
                isPasswordVisible = false
            ),
            onAction = {},
        )
    }
}
