package com.todoapp.uikit.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalLightColors = staticCompositionLocalOf { lightColors() }
internal val LocalDarkColors = staticCompositionLocalOf { darkColors() }

internal fun lightColors(
    red: Color = Color(0xFFFFB1B5),
    crossRed: Color = Color(0xFFB2282D),
    statusCardGray: Color = Color(0xFF6E7180),
    bgColorPurple: Color = Color(0xFFEFF2FF),
    bgColor: Color = Color.White,
    lightPurple: Color = Color(0xFFA9BAFF),
    gray: Color = Color(0xFF717171),
    darkPurple: Color = Color(0xFF1C3082),
    purple: Color = Color(0xFF4566EC),
    black: Color = Color(0xFF090E23),
    brown: Color = Color(0xFF3F2D20),
    green: Color = Color(0xFF84BD93),
    orange: Color = Color(0xFFEF8829),
    darkBrown: Color = Color(0xFF73665C),
    lightBrown: Color = Color(0xFFD3BBAA),
    beige: Color = Color(0xFFE6DCCD),
    lightOrange: Color = Color(0xFFFFE2CD),
    lightYellow: Color = Color(0xFFFFF5E0),
    white: Color = Color(0xFFFFFAF0),
    softPink: Color = Color(0xFFF5D3BB),
    lightGray: Color = Color(0xFFC0C0C0),
): TDColor =
    TDColor(
        red = red,
        crossRed = crossRed,
        statusCardGray = statusCardGray,
        bgColorPurple = bgColorPurple,
        bgColor = bgColor,
        gray = gray,
        black = black,
        darkPurple = darkPurple,
        purple = purple,
        brown = brown,
        green = green,
        orange = orange,
        darkBrown = darkBrown,
        lightBrown = lightBrown,
        beige = beige,
        lightOrange = lightOrange,
        lightYellow = lightYellow,
        white = white,
        softPink = softPink,
        lightGray = lightGray,
        lightPurple = lightPurple,
    )

internal fun darkColors( // will be changed
    red: Color = Color(0xFFFFB1B5),
    crossRed: Color = Color(0xFFB2282D),
    statusCardGray: Color = Color(0xFF6E7180),
    bgColorPurple: Color = Color(0xFFEFF2FF),
    bgColor: Color = Color.Black,
    lightPurple: Color = Color(0xFFA9BAFF),
    gray: Color = Color(0xFF717171),
    darkPurple: Color = Color(0xFF1C3082),
    purple: Color = Color(0xFF4566EC),
    black: Color = Color(0xFF090E23),
    brown: Color = Color(0xFF3F2D20),
    green: Color = Color(0xFF84BD93),
    orange: Color = Color(0xFFEF8829),
    darkBrown: Color = Color(0xFF73665C),
    lightBrown: Color = Color(0xFFD3BBAA),
    beige: Color = Color(0xFFE6DCCD),
    lightOrange: Color = Color(0xFFFFE2CD),
    lightYellow: Color = Color(0xFFFFF5E0),
    white: Color = Color(0xFFFFFAF0),
    softPink: Color = Color(0xFFF5D3BB),
    lightGray: Color = Color(0xFFC0C0C0),
): TDColor =
    TDColor(
        red = red,
        crossRed = crossRed,
        statusCardGray = statusCardGray,
        bgColorPurple = bgColorPurple,
        bgColor = bgColor,
        gray = gray,
        black = black,
        purple = purple,
        darkPurple = darkPurple,
        brown = brown,
        green = green,
        orange = orange,
        darkBrown = darkBrown,
        lightBrown = lightBrown,
        beige = beige,
        lightOrange = lightOrange,
        lightYellow = lightYellow,
        white = white,
        softPink = softPink,
        lightGray = lightGray,
        lightPurple = lightPurple,
    )

class TDColor(
    red: Color,
    crossRed: Color,
    statusCardGray: Color,
    bgColorPurple: Color,
    bgColor: Color,
    lightPurple: Color,
    gray: Color,
    black: Color,
    darkPurple: Color,
    purple: Color,
    brown: Color,
    green: Color,
    orange: Color,
    darkBrown: Color,
    lightBrown: Color,
    beige: Color,
    lightOrange: Color,
    lightYellow: Color,
    white: Color,
    softPink: Color,
    lightGray: Color,
) {
    private var _red: Color by mutableStateOf(red)
    val red: Color = _red
    private var _crossRed: Color by mutableStateOf(crossRed)
    val crossRed: Color = _crossRed
    private var _statusCardGray: Color by mutableStateOf(statusCardGray)
    val statusCardGray: Color = _statusCardGray
    private var _bgColorPurple: Color by mutableStateOf(bgColorPurple)
    val bgColorPurple: Color = _bgColorPurple
    private var _darkPurple: Color by mutableStateOf(darkPurple)
    val darkPurple: Color = _darkPurple
    private var _bgColor: Color by mutableStateOf(bgColor)
    val bgColor: Color = _bgColor
    private var _lightPurple: Color by mutableStateOf(lightPurple)
    val lightPurple: Color = _lightPurple
    private var _lightGray: Color by mutableStateOf(lightGray)
    val lightGray: Color = _lightGray
    private var _gray: Color by mutableStateOf(value = gray)
    val gray: Color = _gray
    private var _purple: Color by mutableStateOf(purple)
    val purple: Color = _purple
    private var _black: Color by mutableStateOf(black)
    val black: Color = _black

    private var _brown: Color by mutableStateOf(brown)
    val brown: Color = _brown

    private var _green: Color by mutableStateOf(green)
    val green: Color = _green

    private var _orange: Color by mutableStateOf(orange)
    val orange: Color = _orange

    private var _darkBrown: Color by mutableStateOf(darkBrown)
    val darkBrown: Color = _darkBrown

    private var _lightBrown: Color by mutableStateOf(lightBrown)
    val lightBrown: Color = _lightBrown

    private var _beige: Color by mutableStateOf(beige)
    val beige: Color = _beige

    private var _lightOrange: Color by mutableStateOf(lightOrange)
    val lightOrange: Color = _lightOrange

    private var _lightYellow: Color by mutableStateOf(lightYellow)
    val lightYellow: Color = _lightYellow

    private var _white: Color by mutableStateOf(white)
    val white: Color = _white

    private var _softPink: Color by mutableStateOf(softPink)
    val softPink: Color = _softPink
}
