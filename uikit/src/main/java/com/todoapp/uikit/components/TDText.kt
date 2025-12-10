package com.todoapp.uikit.components

import android.annotation.SuppressLint
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object TDFontSizes {
    val Small = 12.sp
    val Medium = 16.sp
    val Large = 22.sp
    val XLarge = 32.sp
}
object TDFontWeights{
    val Regular = FontWeight.Normal
    val Medium = FontWeight.Medium
    val Bold = FontWeight.Bold
}
@Composable
fun TDSmallText(
    text: String,
    fontWeight: FontWeight = TDFontWeights.Regular
) {
    Text(
        text = text,
        fontSize = TDFontSizes.Small,
        fontWeight = fontWeight
    )
}

@Composable
fun TDText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TDFontSizes.Medium,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = TDFontWeights.Regular,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        autoSize = autoSize,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@Preview(backgroundColor = android.graphics.Color.WHITE, showBackground = true)
@Composable
fun TDTextExample(){
    TDText(
        text = "This is a text",
        fontSize = TDFontSizes.Large,
        fontWeight = TDFontWeights.Regular,
        style = TextStyle()
    )
}