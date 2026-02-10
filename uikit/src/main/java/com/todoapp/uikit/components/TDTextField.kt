package com.todoapp.uikit.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.previews.TDPreviewForm
import com.todoapp.uikit.theme.TDTheme
import com.todoapp.uikit.theme.textFieldColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TDTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
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
        label = {
            if (label != null) {
                Text(text = label)
            }
        },
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        leadingIcon = leadingIcon,
        trailingIcon = effectiveTrailingIcon,
        visualTransformation = trailingTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
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

@Composable
fun TDLabeledTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    style: TextStyle = TDTheme.typography.regularTextStyle,
    color: Color = TDTheme.colors.gray,
) {
    Column(modifier = modifier) {
        TDText(
            text = title,
            style = style,
            color = color,
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            singleLine = singleLine,
            colors = textFieldColors(),
            visualTransformation = visualTransformation,
            placeholder = {
                if (placeholder != null) TDText(text = placeholder, color = TDTheme.colors.onBackground)
            },
            textStyle = TDTheme.typography.regularTextStyle,
        )
    }
}

@Composable
fun TDCompactOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true,
    style: TextStyle = TDTheme.typography.regularTextStyle,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    color: Color = TDTheme.colors.onBackground,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(4.dp),
    height: Dp = 40.dp
) {
    Column(modifier = modifier.padding(vertical = 6.dp)) {
        if (!label.isNullOrEmpty()) {
            TDText(
                text = label,
                style = style,
                color = color,
            )
            Spacer(Modifier.height(8.dp))
        }
        val borderColor =
            when {
                isError -> TDTheme.colors.red
                !enabled -> TDTheme.colors.gray.copy(alpha = 0.3f)
                else -> TDTheme.colors.onBackground
            }

        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(enabled) {
            if (enabled) {
                focusRequester.requestFocus()
                delay(50)
                keyboardController?.show()
            }
        }

        Column {
            Box(
                modifier =
                    Modifier
                        .height(height = height)
                        .heightIn(min = 40.dp)
                        .border(1.dp, borderColor, roundedCornerShape),
                contentAlignment = Alignment.CenterStart,
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = singleLine,
                    visualTransformation = visualTransformation,
                    textStyle =
                        TDTheme.typography.regularTextStyle.copy(
                            color =
                                if (enabled) {
                                    TDTheme.colors.onBackground
                                } else {
                                    TDTheme.colors.onBackground.copy(alpha = 0.38f)
                                },
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 0.dp)
                                    .then(
                                        if (enabled) {
                                            Modifier
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = MutableInteractionSource()
                                                ) {
                                                    focusRequester.requestFocus()
                                                    coroutineScope.launch {
                                                        delay(50)
                                                        keyboardController?.show()
                                                    }
                                                }
                                        } else {
                                            Modifier
                                        },
                                    ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            leadingIcon?.invoke()

                            Box(
                                modifier =
                                    Modifier
                                        .padding(
                                            vertical = 8.dp,
                                            horizontal = 12.dp,
                                        )
                                        .weight(1f),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (value.isEmpty() && !placeholder.isNullOrEmpty()) {
                                    TDText(
                                        text = placeholder,
                                        color =
                                            if (enabled) {
                                                TDTheme.colors.gray
                                            } else {
                                                TDTheme.colors.gray.copy(alpha = 0.38f)
                                            },
                                    )
                                }
                                innerTextField()
                            }

                            trailingIcon?.invoke()
                        }
                    },
                )
            }
            if (!supportingText.isNullOrEmpty()) {
                Spacer(Modifier.height(4.dp))
                TDText(
                    text = supportingText,
                    style = TDTheme.typography.subheading3,
                    color = if (isError) TDTheme.colors.crossRed else TDTheme.colors.gray,
                )
            }
        }
    }
}

@TDPreviewForm
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
            value = "Doing Homework",
            onValueChange = {},
            label = "",
            leadingIcon = null,
        )

        TDTextField(
            value = "johndoe@hotmail.com",
            onValueChange = {},
            label = "",
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

        Spacer(modifier = Modifier.height(8.dp))

        TDLabeledTextField(
            title = "Title",
            value = "Doing Homework",
            onValueChange = {},
            placeholder = "Placeholder",
            isError = true,
            enabled = false,
            singleLine = false,
        )
        Spacer(modifier = Modifier.height(8.dp))
        TDCompactOutlinedTextField(
            value = "CompactOutlinedTextField",
            onValueChange = { },
            placeholder = "Task Title",
            isError = false,
            label = "Task title",
            supportingText = "Example Error"
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun TextFieldPreview_Dark() {
    var passwordVisible by remember { mutableStateOf(false) }

    TDTheme {
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
                value = "Doing Homework",
                onValueChange = {},
                label = "",
                leadingIcon = null,
            )

            TDTextField(
                value = "johndoe@hotmail.com",
                onValueChange = {},
                label = "",
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

            Spacer(modifier = Modifier.height(8.dp))

            TDLabeledTextField(
                title = "Title",
                value = "Doing Homework",
                onValueChange = {},
                placeholder = "Placeholder",
                isError = true,
                enabled = false,
                singleLine = false,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TDCompactOutlinedTextField(
                value = "CompactOutlinedTextField",
                onValueChange = { },
                placeholder = "Task Title",
                isError = false,
                label = "Task title",
                supportingText = "Example Error",
            )
        }
    }
}

@TDPreviewForm
@Composable
private fun TDCompactOutlinedTextFieldPreview_Filled_Error() {
    TDTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TDCompactOutlinedTextField(
                value = "Read 10 pages",
                label = "Task Title",
                isError = true,
                onValueChange = {},
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = TDTheme.colors.gray,
                        modifier =
                            Modifier
                                .padding(end = 8.dp),
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null,
                        tint = TDTheme.colors.gray,
                        modifier =
                            Modifier
                                .padding(start = 8.dp)
                                .size(24.dp),
                    )
                },
                supportingText = "Example Error",
            )
        }
    }
}
