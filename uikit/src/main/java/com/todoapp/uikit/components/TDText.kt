package com.todoapp.uikit.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme


@Composable
fun TDText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = TDTheme.colors.black,
    style: TextStyle = TDTheme.typography.regularTextStyle,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        textAlign = textAlign,
        style = style.merge(
            color = color,
            fontFamily = FontFamily(
                Font(R.font.poppins_regular, FontWeight.Normal),
                Font(R.font.poppins_medium, FontWeight.Medium),
                Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
                Font(R.font.poppins_bold, FontWeight.Bold),
            ),
        ),
        overflow = overflow,
        maxLines = maxLines,
    )
}

@Composable
fun TDText(
    modifier: Modifier = Modifier,
    fullText: String,
    spanText: String,
    color: Color = TDTheme.colors.brown,
    style: TextStyle = TDTheme.typography.regularTextStyle,
    spanStyle: SpanStyle = SpanStyle(),
    textAlign: TextAlign? = null,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = style.toSpanStyle()) {
                append(fullText)
                val mStartIndex = fullText.indexOf(spanText)
                val mEndIndex = mStartIndex.plus(spanText.length)
                addStyle(
                    style = spanStyle,
                    start = mStartIndex,
                    end = mEndIndex,
                )
            }
        },
        modifier = modifier,
        textAlign = textAlign,
        style = style.merge(
            color = color,
            fontFamily = FontFamily(
                Font(R.font.poppins_regular, FontWeight.Normal),
                Font(R.font.poppins_medium, FontWeight.Medium),
                Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
                Font(R.font.poppins_bold, FontWeight.Bold),
            ),
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun TDTextExample() {
    TDText(
        text = "This is a text.",
    )
}

@Preview(showBackground = true)
@Composable
fun TDAnnotatedTextExample() {

    TDText(
        fullText = "This should be a text.",
        spanText = "should",
        spanStyle = SpanStyle(
            color = TDTheme.colors.gray,
            fontWeight = FontWeight.Bold
        )
    )
}