package com.todoapp.mobile.ui.forgotpassword

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordContract.UiAction
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun ForgotPasswordScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    ForgotPasswordContent(uiState, onAction)
}

@Composable
private fun ForgotPasswordContent(
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
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(color = TDTheme.colors.white.copy(alpha = 0.25f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_lock_reset),
                    contentDescription = stringResource(R.string.logo),
                    modifier = Modifier.size(40.dp),
                    tint = TDTheme.colors.white
                )
            }
            Spacer(Modifier.height(8.dp))
            TDText(
                text = stringResource(R.string.forgot_password),
                style = TDTheme.typography.heading1,
                color = TDTheme.colors.white
            )
        }

        Column(
            Modifier
                .weight(2f)
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                .background(color = TDTheme.colors.white)
                .padding(start = 32.dp, end = 32.dp, top = 24.dp)
                .verticalScroll(verticalScroll)
        ) {
            Spacer(Modifier.height(32.dp))
            TDText(text = stringResource(R.string.recover_access), style = TDTheme.typography.heading1)
            Spacer(Modifier.height(16.dp))
            TDText(
                text = stringResource(
                    R.string.enter_your_email_address_and_we_will_send_you_a_link_to_reset_your_password
                ),
                color = TDTheme.colors.gray.copy(0.7f)
            )
            Spacer(Modifier.height(16.dp))
            TDCompactOutlinedTextField(
                value = uiState.email,
                enabled = uiState.isEmailFieldEnabled,
                label = stringResource(R.string.email),
                onValueChange = { onAction(UiAction.OnEmailChange(it)) },
                placeholder = stringResource(R.string.email),
                isError = uiState.error != null,
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_mail_white),
                        contentDescription = stringResource(R.string.email),
                        tint = TDTheme.colors.gray.copy(0.5f)
                    )
                },
                roundedCornerShape = RoundedCornerShape(12.dp),
                height = 50.dp,
                modifier = Modifier.clickable(indication = null, interactionSource = MutableInteractionSource()) {
                    onAction(UiAction.OnEmailFieldTap)
                }
            )
            uiState.error?.let {
                TDText(text = it, color = TDTheme.colors.red)
            }
            Spacer(Modifier.height(24.dp))
            TDButton(
                text = stringResource(R.string.send_reset_link),
                fullWidth = true,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                onAction(UiAction.OnForgotPasswordTap)
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TDText(text = stringResource(R.string.remember_your_password), color = TDTheme.colors.gray.copy(0.6f))
                TDText(
                    text = stringResource(R.string.back_to_login),
                    color = TDTheme.colors.primary,
                    style = TDTheme.typography.heading6,
                    modifier = Modifier.clickable {
                        onAction(UiAction.OnBackToLoginTap)
                    }
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(
    name = "Forgot Password – Empty",
    showBackground = true,
)
@Composable
private fun ForgotPasswordContentPreview() {
    ForgotPasswordContent(
        uiState = UiState(
            email = ""
        ),
        onAction = {}
    )
}
