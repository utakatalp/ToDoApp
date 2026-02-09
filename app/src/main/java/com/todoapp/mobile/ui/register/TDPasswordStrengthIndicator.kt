package com.todoapp.mobile.ui.register

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.common.passwordValidation.PasswordStrength
import com.todoapp.mobile.common.passwordValidation.toProgress
import com.todoapp.mobile.ui.register.RegisterContract.UiState
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDPasswordStrengthIndicator(
    uiState: UiState,
) {
    uiState.passwordStrength?.let { strength ->
        val (progress, color, label) = strength.toProgress()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { progress },
                color = color,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row {
                TDText(
                    text = label.substringBeforeLast(" ") + " ",
                    style = TDTheme.typography.subheading4,
                    color = TDTheme.colors.onBackground
                )
                TDText(
                    text = label.substringAfterLast(" "),
                    style = TDTheme.typography.subheading4,
                    color = color
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Password Strength")
@Composable
fun TDPasswordStrengthIndicatorPreview() {
    TDTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TDPasswordStrengthIndicator(
                uiState = UiState(
                    passwordStrength = PasswordStrength.WEAK
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            TDPasswordStrengthIndicator(
                uiState = UiState(
                    passwordStrength = PasswordStrength.MEDIUM
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            TDPasswordStrengthIndicator(
                uiState = UiState(
                    passwordStrength = PasswordStrength.STRONG
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Password Strength - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TDPasswordStrengthIndicatorDarkPreview() {
    TDTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TDPasswordStrengthIndicator(
                uiState = UiState(
                    passwordStrength = PasswordStrength.WEAK
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            TDPasswordStrengthIndicator(
                uiState = UiState(
                    passwordStrength = PasswordStrength.MEDIUM
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            TDPasswordStrengthIndicator(
                uiState = UiState(
                    passwordStrength = PasswordStrength.STRONG
                )
            )
        }
    }
}
