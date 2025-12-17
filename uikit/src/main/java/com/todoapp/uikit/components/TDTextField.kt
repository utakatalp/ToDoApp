package com.todoapp.uikit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.theme.textFieldColors

@Composable
fun TDTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    passwordVisible: Boolean? = null,
    onTogglePasswordVisible: (() -> Unit)? = null,
    onFocusChange: ((Boolean) -> Unit)? = null,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            bringIntoViewRequester.bringIntoView()
        }
    }

    val effectiveTrailingIcon: (@Composable () -> Unit)? =
        trailingIcon
            ?: if (passwordVisible != null && onTogglePasswordVisible != null) {
                {
                    IconButton(onClick = onTogglePasswordVisible) {
                        Icon(
                            imageVector =
                                ImageVector.vectorResource(
                                    if (passwordVisible) {
                                        R.drawable.ic_visibility
                                    } else {
                                        R.drawable.ic_visibility_off
                                    },
                                ),
                            contentDescription = null,
                        )
                    }
                }
            } else {
                null
            }

    val trailingTransformation =
        if ((passwordVisible != null) && (onTogglePasswordVisible != null) && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            visualTransformation
        }

    OutlinedTextField(
        modifier =
            modifier
                .fillMaxWidth()
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    onFocusChange?.invoke(focusState.isFocused)
                },
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        leadingIcon = leadingIcon,
        trailingIcon = effectiveTrailingIcon,
        visualTransformation = trailingTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(12.dp),
        colors = textFieldColors(),
        supportingText = {
            if (!supportingText.isNullOrEmpty()) {
                Text(
                    text = supportingText,
                    color =
                        if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TextFieldPreview() {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
    ) {
        TDTextField(
            value = "john",
            onValueChange = {},
            label = "First Name",
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_name),
                    contentDescription = null,
                )
            },
        )

        Spacer(modifier = Modifier.height(4.dp))

        TDTextField(
            value = "doe",
            onValueChange = {},
            label = "Last Name",
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_name),
                    contentDescription = null,
                )
            },
        )

        TDTextField(
            value = "johndoe@hotmail.com",
            onValueChange = {},
            label = "Email",
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mail),
                    contentDescription = null,
                )
            },
        )

        TDTextField(
            value = "Example Password",
            supportingText = "Example Error",
            onValueChange = {},
            label = "Password",
            isError = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password),
                    contentDescription = null,
                )
            },
            passwordVisible = passwordVisible,
            onTogglePasswordVisible = { passwordVisible = !passwordVisible },
        )
    }
}
