package io.github.skydynamic.maiproberplus.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import io.github.skydynamic.maiproberplus.Application.Companion.application
import java.nio.charset.StandardCharsets

enum class TextAlign(val align: Paint.Align) {
    Left(Paint.Align.LEFT),
    Center(Paint.Align.CENTER),
    Right(Paint.Align.RIGHT)
}

class TextOutline(
    val color: Int,
    val weight: Float,
)

fun Canvas.drawImage(bitmap: Bitmap, x: Float, y: Float) {
    this.drawBitmap(bitmap, x, y, null)
}

private fun truncateTextToFitWidth(text: String, maxWidth: Float, paint: Paint): String {
    var truncatedText = text
    var textWidth = paint.measureText(truncatedText)

    while (textWidth > maxWidth && truncatedText.isNotEmpty()) {
        truncatedText = truncatedText.dropLast(1)
        textWidth = paint.measureText(truncatedText)
    }

    if (truncatedText.length < text.length) {
        truncatedText += "..."
        textWidth = paint.measureText(truncatedText)
        if (textWidth > maxWidth) {
            truncatedText = truncatedText.dropLast(2) + "..."
        }
    }

    return truncatedText
}

fun Canvas.drawText(
    text: String,
    textSize: Float,
    x: Float,
    y: Float,
    maxText: Int = Int.MAX_VALUE,
    font: Int? = null,
    color: Int? = null,
    outline: TextOutline? = null,
    align: TextAlign = TextAlign.Left
) {
    val paint = Paint()

    paint.color = color ?: Color.BLACK
    paint.textAlign = align.align
    paint.textSize = textSize
    paint.typeface = if (font != null) {
        application.getFont(font)
    } else {
        Typeface.DEFAULT_BOLD
    }

    outline?.let {
        val outlinePaint = Paint(paint)
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.strokeWidth = it.weight
        outlinePaint.color = it.color

        this.drawText(text, x, y, outlinePaint)
    }

    val truncatedText = truncateTextToFitWidth(text, maxText.toFloat(), paint)

    this.drawText(truncatedText, x, y, paint)
}