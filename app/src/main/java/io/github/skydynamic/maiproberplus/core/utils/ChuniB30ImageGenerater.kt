package io.github.skydynamic.maiproberplus.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.icu.math.BigDecimal
import android.icu.text.DecimalFormat
import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniData
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.bests.BestsImageGenerateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat

object ChuniB30ImageGenerater {
    private val decimalFormat = DecimalFormat("0.00").apply {
        roundingMode = BigDecimal.ROUND_DOWN
    }

    private val ratingNumXPosList = listOf<Float>(
        255f, 271f, 283f, 295f, 311f
    ).reversed()

    private fun getPlayerRating(b30: List<ChuniScoreEntity>, r10: List<ChuniScoreEntity>): Double {
        return (b30.sumOf { decimalFormat.format(it.rating).toDouble() }
                + r10.sumOf { decimalFormat.format(it.rating).toDouble() }
                ) / 40
    }

    private fun getRatingColor(rating: Double): String = when {
        rating < 4 -> "green"
        rating < 7 -> "orange"
        rating < 9 -> "red"
        rating < 12 -> "murasaki"
        rating < 13.25 -> "bronze"
        rating < 14.49 -> "silver"
        rating < 15.25 -> "gold"
        rating < 16 -> "platinum"
        rating < 17 -> "rainbow"
        rating >= 17 -> "r17"
        else -> "rainbow"
    }

    private fun getDifficultyFrame(difficulty: ChuniEnums.Difficulty): Bitmap {
        val bitmap = when (difficulty) {
            ChuniEnums.Difficulty.BASIC ->
                application.getBitmapFromDrawable(R.drawable.ic_chuni_frame_basic)
            ChuniEnums.Difficulty.ADVANCED ->
                application.getBitmapFromDrawable(R.drawable.ic_chuni_frame_advanced)
            ChuniEnums.Difficulty.EXPERT ->
                application.getBitmapFromDrawable(R.drawable.ic_chuni_frame_expert)
            ChuniEnums.Difficulty.MASTER ->
                application.getBitmapFromDrawable(R.drawable.ic_chuni_frame_master)
            ChuniEnums.Difficulty.ULTIMA ->
                application.getBitmapFromDrawable(R.drawable.ic_chuni_frame_ultima)
            else -> application.getBitmapFromDrawable(R.drawable.ic_chuni_frame_basic)
        }
        return createScaledBitmapHighQuality(bitmap, 282, 121)
    }

    private fun getRankTypeBitmap(type: ChuniEnums.RankType): Bitmap {
        return createScaledBitmapHighQuality(
            application.getBitmapFromDrawable(type.imageId), 55, 22)
    }

    private fun getShougouBitmap(color: String): Bitmap {
        return createScaledBitmapHighQuality(when (color.lowercase()) {
            "normal" -> application.getBitmapFromDrawable(R.drawable.ic_chuni_shougou_normal)
            "bronze" -> application.getBitmapFromDrawable(R.drawable.ic_chuni_shougou_bronze)
            "silver" -> application.getBitmapFromDrawable(R.drawable.ic_chuni_shougou_silver)
            "gold" -> application.getBitmapFromDrawable(R.drawable.ic_chuni_shougou_gold)
            "rainbow" -> application.getBitmapFromDrawable(R.drawable.ic_chuni_shougou_rainbow)
            else -> application.getBitmapFromDrawable(R.drawable.ic_chuni_shougou_normal)
        }, 538, 50)
    }

    private fun getRatingNumIcoList(rating: Double, color: String): List<Bitmap> {
        val ratingList = mutableListOf<Bitmap>()
        val rating = decimalFormat.format(rating)
        val raString = if (rating.toDouble() < 10) {
            " $rating"
        } else rating.toString()
        for (string in raString.toString()) {
            ratingList += if (string != ' ') {
                createScaledBitmapHighQuality(
                    application.getImageFromAssets(
                        "chunithm/num/${
                            if (string == '.') "point" else string
                        }_$color.png"
                    ) ?: Bitmap.createBitmap(18, 24, Bitmap.Config.ALPHA_8), 18, 24
                )
            } else {
                createScaledBitmapHighQuality(
                    Bitmap.createBitmap(18, 24, Bitmap.Config.ALPHA_8), 18, 24
                )
            }
        }
        return ratingList.reversed()
    }

    private fun generateSingleSongBestCard(
        index: Int,
        score: ChuniScoreEntity
    ): Bitmap {
        val songInfo = ChuniData.CHUNI_SONG_LIST.filter { it.id == score.songId }.first()
        val frameBitmap = getDifficultyFrame(score.diff)

        val resultBitmap = Bitmap.createBitmap(
            frameBitmap.width,
            frameBitmap.height,
            frameBitmap.config
        )

        val fontColor = Color.WHITE

        val canvas = Canvas(resultBitmap)

        canvas.drawImage(frameBitmap, 0f, 0f)

        val icon = runBlocking {
            createScaledBitmapHighQuality(
                NetworkImageRequestUtil.getImageBitmap(
                    "https://assets2.lxns.net/chunithm/jacket/${score.songId}.png"
                ) ?: application.getBitmapFromDrawable(R.drawable.ic_maimai_jacket_default), // 找不到用什么默认图了，拿舞萌的吧
                60, 60)
        }

        canvas.drawImage(icon, 17f, 17f)

        canvas.drawText(
            songInfo.title,
            16f, 86f, 28f, 172,
            font = R.font.source_han_momo_hc_m,
            color = fontColor
        )
        canvas.drawText(
            "ID ${songInfo.id}",
            12f, 87f, 53f,
            font = R.font.fot_b,
            color = fontColor
        )
        canvas.drawText(
            "${NumberFormat.getNumberInstance().format(score.score)}",
            20f, 86f, 78f,
            font = R.font.fot_b,
            color = fontColor
        )
        canvas.drawImage(getRankTypeBitmap(score.rankType), 211f, 61f)
        canvas.drawText(
            "#$index",
            14f, 18f, 105f,
            font = R.font.fot_eb
        )
        canvas.drawText(
            "${score.level} ▶",
            14f, 107f, 105f,
            font = R.font.fot_b,
            align = TextAlign.Right
        )
        canvas.drawText(
            "${decimalFormat.format(score.rating)}",
            14f, 111f, 105f,
            font = R.font.fot_eb
        )
        if (score.fullComboType != ChuniEnums.FullComboType.NULL) {
            canvas.drawImage(
                createScaledBitmapHighQuality(
                    application.getBitmapFromDrawable(score.fullComboType.imageId), 103, 16
                ),
                168f, 90f
            )
        }

        return resultBitmap
    }

    fun generateBestsImage(
        scores: List<ChuniScoreEntity>,
    ) : Bitmap {
        val startTime = System.currentTimeMillis()

        val config = application.configManager.config

        val b30Score = scores.filter { !it.recent }
            .sortedByDescending { it.rating }
            .take(30)

        val r10Score = scores.filter { it.recent }
            .sortedByDescending { it.rating }
            .take(10)

        val rating = getPlayerRating(b30Score, r10Score)

        val bgBitmap = createScaledBitmapHighQuality(
            application.getBitmapFromDrawable(R.drawable.ic_chuni_b30_bg), 1440, 1360)

        val b30cacheDir = application.filesDir.resolve("b30cache")
        b30cacheDir.mkdirs()
        val outputStream = b30cacheDir.resolve("chunithm_b10_$startTime.jpg").outputStream()

        val resultBitmap = Bitmap.createBitmap(
            bgBitmap.width,
            bgBitmap.height,
            bgBitmap.config
        )

        val canvas = Canvas(resultBitmap)

        canvas.drawImage(bgBitmap, 0f, 0f)

        val plateBitmap = createScaledBitmapHighQuality(
            application.getBitmapFromDrawable(R.drawable.ic_chuni_plate_default), 576, 228
        )
        val playerNamePlateBitmap = createScaledBitmapHighQuality(
            application.getBitmapFromDrawable(R.drawable.ic_chuni_b30_name), 340, 100
        )
        val shougouBitmap = getShougouBitmap(config.userInfo.shougouColor)
        val iconMaskBitmap = createScaledBitmapHighQuality(
            application.getBitmapFromDrawable(R.drawable.ic_chuni_iconmask), 80, 80
        )
        val iconBitmap = runBlocking {
            createScaledBitmapHighQuality(
                NetworkImageRequestUtil.getImageBitmap(
                    "https://assets2.lxns.net/chunithm/character/" +
                            "${config.userInfo.chuniCharacter}.png",
                ) ?: application.getBitmapFromDrawable(R.drawable.ic_chuni_icon_default), 80, 80
            )
        }
        val ratingColor = getRatingColor(rating)
        val ratingBitmap = createScaledBitmapHighQuality(
            application.getImageFromAssets("chunithm/num/rating_$ratingColor.png")
                ?: Bitmap.createBitmap(70, 16, Bitmap.Config.ALPHA_8), 70, 16
        )
        val ratingNumIcoList = getRatingNumIcoList(rating, ratingColor)

        canvas.drawImage(plateBitmap, 25f, 31f)
        canvas.drawImage(playerNamePlateBitmap, 160f, 113f)
        canvas.drawImage(shougouBitmap, 104f, 71f)
        canvas.drawText(
            config.userInfo.shougou,
            20f, 373f, 102f, 390,
            R.font.source_han_sans_37,
            align = TextAlign.Center
        )
        canvas.drawText(
            "Lv.",
            20f, 175f, 160f,
            font = R.font.fot_b
        )
        canvas.drawText(
            "**",
            30f, 206f, 160f,
            font = R.font.fot_b
        )
        canvas.drawText(
            config.userInfo.name.toHalfWidth(),
            32f, 256f, 157f, 220,
            font = R.font.source_han_sans_37,
            overflow = TextOverflow.SCALE_DOWN_TEXT
        )
        canvas.drawImage(iconMaskBitmap, 495f, 119f)
        canvas.drawImage(iconBitmap, 495f, 119f)
        canvas.drawImage(ratingBitmap, 175f, 180f)
        ratingNumIcoList.forEachIndexed { index, bitmap ->
            canvas.drawImage(bitmap, ratingNumXPosList[index], 173f)
        }
        canvas.drawText(
            "${decimalFormat.format(b30Score.sumOf { it.rating.toDouble() } / 30)}",
            24f, 835f, 97f,
            font = R.font.fot_b,
            align = TextAlign.Center,
            color = Color.rgb(30, 54, 99)
        )
        canvas.drawText(
            "${decimalFormat.format(r10Score.sumOf { it.rating.toDouble() / 10})}",
            24f, 835f, 137f,
            font = R.font.fot_b,
            align = TextAlign.Center,
            color = Color.rgb(30, 54, 99)
        )
        canvas.drawText(
            "${
                if (r10Score.isNotEmpty()) decimalFormat.format(
                    ((r10Score.first().rating * 10) + b30Score.sumOf { it.rating.toDouble() }) / 40)
                else decimalFormat.format(0.0)
            }",
            24f, 835f, 177f,
            font = R.font.fot_b,
            align = TextAlign.Center,
            color = Color.rgb(30, 54, 99)
        )

        val drawJobs = mutableListOf<Job>()

        var lineIndex = 0
        var drawY = 274f
        b30Score.forEachIndexed { index, score ->
            val currentLineIndex = lineIndex
            val currentDrawY = drawY
            drawJobs += GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                canvas.drawImage(
                    generateSingleSongBestCard(index + 1, score), 19f + currentLineIndex * 280, currentDrawY
                )
            }
            lineIndex++
            if (lineIndex == 5) {
                drawY += 120
                lineIndex = 0
            }
        }

        lineIndex = 0
        drawY = 1049f
        r10Score.forEachIndexed { index, score ->
            val currentLineIndex = lineIndex
            val currentDrawY = drawY
            drawJobs += GlobalViewModel.viewModelScope.launch(Dispatchers.IO) {
                canvas.drawImage(
                    generateSingleSongBestCard(index + 1, score), 19f + currentLineIndex * 280, currentDrawY
                )
            }
            lineIndex++
            if (lineIndex == 5) {
                drawY += 120
                lineIndex = 0
            }
        }

        runBlocking {
            drawJobs.joinAll()
        }

        resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()

        val endTime = System.currentTimeMillis()
        Log.d("ChunithmB30GenerateUtil", "generateChunithmB30Bitmap: ${(endTime - startTime) / 1000}s")
        BestsImageGenerateViewModel.canGenerate = true
        return resultBitmap
    }
}