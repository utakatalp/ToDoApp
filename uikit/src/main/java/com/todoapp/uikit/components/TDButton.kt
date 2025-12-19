package com.todoapp.uikit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uikit.R

enum class TDButtonType { PRIMARY, SECONDARY }

enum class TDButtonSize { SMALL, MEDIUM }

@Composable
fun TDButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean = true,
    type: TDButtonType = TDButtonType.PRIMARY,
    size: TDButtonSize = TDButtonSize.MEDIUM,
    icon: ImageVector? = null,
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
                    fontSize = 16.sp,
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
    val paddingValues =
        when (size) {
            TDButtonSize.SMALL -> PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            TDButtonSize.MEDIUM -> PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        }

    when (type) {
        TDButtonType.PRIMARY -> {
            Button(
                modifier =
                    Modifier
                        .height(height)
                        .then(modifier),
                onClick = onClick,
                enabled = isEnable,
                contentPadding = paddingValues,
                shape = RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    ),
            ) {
                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TDText(
                    text = text,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        TDButtonType.SECONDARY -> {
            Button(
                modifier =
                    Modifier
                        .height(height)
                        .then(modifier),
                onClick = onClick,
                enabled = isEnable,
                contentPadding = paddingValues,
                shape = RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    ),
            ) {
                icon?.let {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        imageVector = icon,
                        contentDescription = text,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TDText(
                    text = text,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TDButtonPreview() {
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
        )
    }
}
