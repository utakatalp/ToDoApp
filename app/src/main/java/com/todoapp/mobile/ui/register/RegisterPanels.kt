package com.todoapp.mobile.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.register.RegisterContract.UiAction
import com.todoapp.mobile.ui.register.RegisterContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
internal fun RegisterBrandingPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(TDTheme.colors.pendingGray)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
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
        TDText(
            text = stringResource(R.string.create_account),
            style = TDTheme.typography.heading1,
            color = TDTheme.colors.white
        )
        TDText(
            modifier = Modifier.size(width = 300.dp, height = 70.dp),
            text = stringResource(R.string.join_us_and_start_organizing_your_tasks_efficiently),
            style = TDTheme.typography.heading4,
            textAlign = TextAlign.Center,
            color = TDTheme.colors.white.copy(0.8f)
        )
    }
}

@Composable
internal fun RegisterFormPanel(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
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
        ) { onAction(UiAction.OnFullNameFieldTap) }
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
        ) { onAction(UiAction.OnEmailFieldTap) }
    )
    uiState.emailError?.let {
        TDText(text = it.message, color = TDTheme.colors.red)
    }
    TDCompactOutlinedTextField(
        value = uiState.password,
        enabled = uiState.isPasswordFieldEnabled,
        label = stringResource(R.string.password),
        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                        if (uiState.isPasswordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_close
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
        ) { onAction(UiAction.OnPasswordFieldTap) }
    )
    uiState.passwordError?.let {
        TDText(text = it.message, color = TDTheme.colors.red)
    }

    Spacer(modifier = Modifier.height(2.dp))

    TDPasswordStrengthIndicator(uiState = uiState)

    TDCompactOutlinedTextField(
        value = uiState.confirmPassword,
        enabled = uiState.isPasswordConfirmationFieldEnabled,
        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
        ) { onAction(UiAction.OnConfirmPasswordFieldTap) }
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
    ) { onAction(UiAction.OnSignUpTap) }
    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = TDTheme.colors.gray.copy(0.3f))
        TDText(
            text = stringResource(R.string.or_continue_with),
            style = TDTheme.typography.subheading4,
            color = TDTheme.colors.gray,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = TDTheme.colors.gray.copy(0.3f))
    }

    Spacer(Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        TDButton(
            text = stringResource(R.string.google),
            fullWidth = false,
            type = TDButtonType.OUTLINE,
            icon = painterResource(R.drawable.ic_google_logo),
            modifier = Modifier.weight(1f)
        ) { onAction(UiAction.OnGoogleSignInTap) }
        Spacer(Modifier.width(12.dp))
        TDButton(
            text = stringResource(R.string.facebook),
            fullWidth = false,
            type = TDButtonType.OUTLINE,
            icon = painterResource(R.drawable.ic_facebook_logo),
            modifier = Modifier.weight(1f)
        ) { onAction(UiAction.OnFacebookSignInTap) }
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
            modifier = Modifier.clickable { onAction(UiAction.OnLoginTap) }
        )
    }
    Spacer(Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            TDText(
                text = stringResource(R.string.by_signing_up_you_agree_to_our),
                style = TDTheme.typography.subheading4,
                color = TDTheme.colors.gray
            )
            TDText(
                text = stringResource(R.string.terms_of_service),
                style = TDTheme.typography.subheading4.copy(textDecoration = TextDecoration.Underline),
                color = TDTheme.colors.gray,
                modifier = Modifier.clickable { onAction(UiAction.OnTermsOfServiceTap) }
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
                style = TDTheme.typography.subheading4.copy(textDecoration = TextDecoration.Underline),
                color = TDTheme.colors.gray,
                modifier = Modifier.clickable { onAction(UiAction.OnPrivacyPolicyTap) }
            )
        }
    }
}
