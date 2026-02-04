package com.todoapp.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uikit.R
import com.todoapp.uikit.previews.TDPreviewNoBg
import com.todoapp.uikit.theme.TDTheme

enum class TDButtonType { PRIMARY, SECONDARY, CANCEL }

enum class TDButtonSize { SMALL, MEDIUM }

@Composable
fun TDButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean = true,
    type: TDButtonType = TDButtonType.PRIMARY,
    size: TDButtonSize = TDButtonSize.MEDIUM,
    icon: Painter? = null,
    fullWidth: Boolean = false,
    onClick: () -> Unit,
) {
    val textStyle =
        when (size) {
            TDButtonSize.SMALL ->
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily =
                        FontFamily(
                            Font(R.font.poppins_regular, FontWeight.Normal),
                            Font(R.font.poppins_medium, FontWeight.Medium),
                            Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
                            Font(R.font.poppins_bold, FontWeight.Bold),
                        ),
                )

            TDButtonSize.MEDIUM ->
                TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily =
                        FontFamily(
                            Font(R.font.poppins_regular, FontWeight.Normal),
                            Font(R.font.poppins_medium, FontWeight.Medium),
                            Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
                            Font(R.font.poppins_bold, FontWeight.Bold),
                        ),
                )
        }
    val height =
        when (size) {
            TDButtonSize.SMALL -> 40.dp
            TDButtonSize.MEDIUM -> 60.dp
        }

    val width =
        when (size) {
            TDButtonSize.SMALL -> 140.dp
            TDButtonSize.MEDIUM -> 200.dp
        }

    val paddingValues =
        when (size) {
            TDButtonSize.SMALL -> PaddingValues(horizontal = 10.dp, vertical = 8.dp)
            TDButtonSize.MEDIUM -> PaddingValues(horizontal = 12.dp, vertical = 12.dp)
        }

    when (type) {
        TDButtonType.PRIMARY -> {
            Button(
                modifier =
                    Modifier
                        .height(height)
                        .then(modifier)
                        .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier.width(width)),
                onClick = onClick,
                enabled = isEnable,
                contentPadding = paddingValues,
                shape = RoundedCornerShape(6.dp),
                colors =
                    ButtonColors(
                        containerColor = TDTheme.colors.purple,
                        contentColor = TDTheme.colors.white,
                        disabledContainerColor = TDTheme.colors.purple.copy(alpha = 0.4f),
                        disabledContentColor = TDTheme.colors.white.copy(alpha = 0.5f),
                    ),
            ) {
                icon?.let {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = icon,
                        contentDescription = text,
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TDText(
                    text = text,
                    style = textStyle,
                    color = TDTheme.colors.white,
                )
            }
        }

        TDButtonType.SECONDARY -> {
            OutlinedButton(
                modifier =
                    Modifier
                        .height(height)
                        .then(modifier)
                        .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier.width(width)),
                onClick = onClick,
                enabled = isEnable,
                contentPadding = paddingValues,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(2.dp, TDTheme.colors.crossRed),
            ) {
                icon?.let {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = icon,
                        contentDescription = text,
                        tint = TDTheme.colors.purple,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TDText(
                    text = text,
                    style = textStyle,
                    color = TDTheme.colors.crossRed,
                )
            }
        }
        TDButtonType.CANCEL -> {
            Button(
                modifier =
                    Modifier
                        .height(height)
                        .then(modifier)
                        .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier.width(width)),
                onClick = onClick,
                enabled = isEnable,
                contentPadding = paddingValues,
                shape = RoundedCornerShape(6.dp),
                colors =
                    ButtonColors(
                        containerColor = TDTheme.colors.red,
                        contentColor = TDTheme.colors.purple,
                        disabledContainerColor = TDTheme.colors.red.copy(alpha = 0.5f),
                        disabledContentColor = TDTheme.colors.purple.copy(alpha = 0.8f),
                    ),
            ) {
                icon?.let {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = icon,
                        contentDescription = text,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TDText(
                    text = text,
                    style = textStyle,
                    color = TDTheme.colors.black,
                )
            }
        }
    }
}

@TDPreviewNoBg
@Composable
fun TDButtonPreview() {
    Column {
        TDButton(
            text = "Primary Button",
            onClick = {},
            type = TDButtonType.PRIMARY,
            size = TDButtonSize.SMALL,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TDButton(
            text = "Primary Button 2",
            onClick = {},
            type = TDButtonType.PRIMARY,
            size = TDButtonSize.MEDIUM,
            fullWidth = true,
            isEnable = false,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TDButton(
            text = "Secondary Button",
            onClick = {},
            type = TDButtonType.SECONDARY,
            size = TDButtonSize.SMALL,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TDButton(
            text = "Secondary Button2",
            onClick = {},
            type = TDButtonType.SECONDARY,
            size = TDButtonSize.MEDIUM,
            fullWidth = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TDButton(
            text = "Cancel Button",
            onClick = {},
            type = TDButtonType.CANCEL,
            size = TDButtonSize.SMALL,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TDButton(
            text = "Cancel Button2",
            onClick = {},
            type = TDButtonType.CANCEL,
            size = TDButtonSize.MEDIUM,
            fullWidth = true,
        )
    }
}
