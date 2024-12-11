package io.github.skydynamic.maiproberplus.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.R
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiData
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.ui.compose.bests.BestsImageGenerateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object MaimaiB50GenerateUtil {
    private data class RatingTableValue(
        val avgRating: String,
        val sumRating: String,
        val maxRating: String,
        val minRating: String,
        val ssspMaxLevel: String,
        val ssspMinLevel: String,
        val sssMaxLevel: String,
        val sssMinLevel: String,
        val sspMaxLevel: String,
        val sspMinLevel: String,
        val ssMaxLevel: String,
        val ssMinLevel: String,
    )

    private val ratingNumXPosList = listOf<Float>(
        295f, 313f, 330f, 348f, 366f
    ).reversed()

    private fun getDanImage(dan: Int): Bitmap =
        Bitmap.createScaledBitmap(
            application.getImageFromAssets("maimai/dan/$dan.png")
                ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8), 104, 48, false
        )

    private fun getRatingFrame(rating: Int): Bitmap {
        val index = when (rating) {
            in 0..999 -> "1"
            in 1000..1999 -> "2"
            in 2000..3999 -> "3"
            in 4000..6999 -> "4"
            in 7000..9999 -> "5"
            in 10000..11999 -> "6"
            in 12000..12999 -> "7"
            in 13000..13999 -> "8"
            in 14000..14499 -> "9"
            in 14500..14999 -> "10"
            in 15000..16999 -> "11"
            else -> "1"
        }
        return Bitmap.createScaledBitmap(
            application.getImageFromAssets("maimai/rating/$index.png")
                ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8),
            225, 44, false
        )
    }

    private fun getDifficultyFrame(difficulty: MaimaiEnums.Difficulty): Bitmap {
        val bitmap = when (difficulty) {
            MaimaiEnums.Difficulty.BASIC ->
                application.getBitmapFromDrawable(R.drawable.ic_maimai_frame_basic)
            MaimaiEnums.Difficulty.ADVANCED ->
                application.getBitmapFromDrawable(R.drawable.ic_maimai_frame_advanced)
            MaimaiEnums.Difficulty.EXPERT ->
                application.getBitmapFromDrawable(R.drawable.ic_maimai_frame_expert)
            MaimaiEnums.Difficulty.MASTER ->
                application.getBitmapFromDrawable(R.drawable.ic_maimai_frame_master)
            MaimaiEnums.Difficulty.REMASTER ->
                application.getBitmapFromDrawable(R.drawable.ic_maimai_frame_remaster)
        }
        return Bitmap.createScaledBitmap(bitmap, 282, 120, false)
    }

    private fun getShougouBitmap(color: String): Bitmap {
        return Bitmap.createScaledBitmap(when (color.lowercase()) {
            "normal" -> application.getBitmapFromDrawable(R.drawable.ic_maimai_shougou_normal)
            "bronze" -> application.getBitmapFromDrawable(R.drawable.ic_maimai_shougou_bronze)
            "sliver" -> application.getBitmapFromDrawable(R.drawable.ic_maimai_shougou_silver)
            "gold" -> application.getBitmapFromDrawable(R.drawable.ic_maimai_shougou_gold)
            "rainbow" -> application.getBitmapFromDrawable(R.drawable.ic_maimai_shougou_rainbow)
            else -> application.getBitmapFromDrawable(R.drawable.ic_maimai_shougou_normal)
        }, 368, 48, false)
    }

    private fun getDxRating(b35: List<MaimaiScoreEntity>, b15: List<MaimaiScoreEntity>): Int {
        return 0 + b35.sumOf { it.rating } + b15.sumOf { it.rating }
    }

    private fun getDxRatingNumIcoList(rating: Int): List<Bitmap> {
        val ratingList = mutableListOf<Bitmap>()
        for (string in rating.toString()) {
            ratingList += Bitmap.createScaledBitmap(application.getImageFromAssets("maimai/num/${string}.png")
                ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8), 19, 22, false)
        }
        return ratingList.reversed()
    }

    private fun getScoresTableValue(scores: List<MaimaiScoreEntity>): RatingTableValue {
        val maxRating = scores.maxWithOrNull(Comparator.comparingInt { it.rating })?.rating ?: 0
        val minRating = scores.minWithOrNull(Comparator.comparingInt { it.rating })?.rating ?: 0

        val ssspScore = scores.filter { it.rankType == MaimaiEnums.RankType.SSSP }
        val ssspMaxLevel = ssspScore.maxWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f
        val ssspMinLevel = ssspScore.minWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f

        val sssScore = scores.filter { it.rankType == MaimaiEnums.RankType.SSS }
        val sssMaxLevel = sssScore.maxWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f
        val sssMinLevel = sssScore.minWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f

        val sspScore = scores.filter { it.rankType == MaimaiEnums.RankType.SSP }
        val sspMaxLevel = sspScore.maxWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f
        val sspMinLevel = sspScore.minWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f

        val ssScore = scores.filter { it.rankType == MaimaiEnums.RankType.SS }
        val ssMaxLevel = ssScore.maxWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f
        val ssMinLevel = ssScore.minWithOrNull(Comparator.comparingInt { it.rating })?.level ?: 0f

        val sumRating = scores.sumOf { it.rating }

        return RatingTableValue(
            avgRating = if (sumRating != 0) DecimalFormat("#.#").format(sumRating / scores.size) else "-",
            sumRating = sumRating.toString(),
            maxRating = maxRating.toString(),
            minRating = if (scores.size == 1) "-" else minRating.toString(),
            ssspMaxLevel = if (ssspMaxLevel < 1.0 || ssspMaxLevel > 15.0) "-" else ssspMaxLevel.toString(),
            ssspMinLevel = if (scores.size == 1 &&(ssspMinLevel < 1.0 || ssspMinLevel > 15.0)) "-" else ssspMinLevel.toString(),
            sssMaxLevel = if (sssMaxLevel < 1.0 || sssMaxLevel > 15.0) "-" else sssMaxLevel.toString(),
            sssMinLevel = if (scores.size == 1 &&(ssspMinLevel < 1.0 || ssspMinLevel > 15.0)) "-" else sssMinLevel.toString(),
            sspMaxLevel = if (sspMaxLevel < 1.0 || sspMaxLevel > 15.0) "-" else sspMaxLevel.toString(),
            sspMinLevel = if (scores.size == 1 &&(ssspMinLevel < 1.0 || ssspMinLevel > 15.0)) "-" else sspMinLevel.toString(),
            ssMaxLevel = if (ssMaxLevel < 1.0 || ssMaxLevel > 15.0) "-" else ssMaxLevel.toString(),
            ssMinLevel = if (scores.size == 1 &&(ssspMinLevel < 1.0 || ssspMinLevel > 15.0)) "-" else ssMinLevel.toString(),
        )
    }


    fun generateSingleSongBestCard(
        index: Int,
        score: MaimaiScoreEntity
    ): Bitmap {
        val songInfo = MaimaiData.MAIMAI_SONG_LIST.filter { it.id == score.songId }.first()
        val totalNote = MaimaiData.getNoteTotal(songInfo.title, score.diff, score.type)
        val dxSatrs = MaimaiData.getDxStar(totalNote, score.dxScore)

        val fontColor = when (score.diff) {
            MaimaiEnums.Difficulty.REMASTER -> Color.rgb(195, 70, 231)
            else -> Color.rgb(255, 255, 255)
        }

        val frameBitmap = getDifficultyFrame(score.diff)

        val resultBitmap = Bitmap.createBitmap(
            frameBitmap.width,
            frameBitmap.height,
            frameBitmap.config
        )

        val canvas = Canvas(resultBitmap)

        canvas.drawImage(frameBitmap, 0f, 0f)

        val icon = runBlocking {
            Bitmap.createScaledBitmap(
                NetworkImageRequestUtil.getImageBitmap(
                    "https://assets2.lxns.net/maimai/jacket/${score.songId}.png"
                ) ?: application.getBitmapFromDrawable(R.drawable.ic_maimai_jacket_default),
                60, 60, false)
        }

        val typeIco = runBlocking {
            Bitmap.createScaledBitmap(when (score.type) {
                MaimaiEnums.SongType.STANDARD ->
                    application.getBitmapFromDrawable(R.drawable.ic_maimai_sd)
                MaimaiEnums.SongType.DX ->
                    application.getBitmapFromDrawable(R.drawable.ic_maimai_dx)
            }, 19, 9, false)
        }

        canvas.drawImage(icon, 17f, 17f)
        canvas.drawImage(typeIco, 19f, 66f)
        canvas.drawText(
            songInfo.title,
            16f, 86f, 28f, 172,
            font = R.font.source_han_sans_35,
            color = fontColor
        )
        canvas.drawText(
            "ID ${songInfo.id}",
            12f, 87f, 53f,
            font = R.font.fot_b,
            color = fontColor
        )
        canvas.drawText(
            "${score.dxScore} / $totalNote",
            12f, 265f, 53f,
            font = R.font.fot_b,
            color = fontColor,
            align = TextAlign.Right
        )
        canvas.drawText(
            "${DecimalFormat("#." + "0".repeat(4)).format(score.achievement)}%",
            20f, 86f, 78f,
            font = R.font.fot_b,
            color = fontColor
        )
        canvas.drawImage(
            Bitmap.createScaledBitmap(
                application.getBitmapFromDrawable(score.rankType.imageId), 56, 25, false
            ), 215f, 58f
        )
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
            "${score.rating}",
            14f, 111f, 105f,
            font = R.font.fot_eb
        )
        if (dxSatrs > 0) {
            canvas.drawImage(
                Bitmap.createScaledBitmap(
                    application.getBitmapFromDrawable(MaimaiData.getDxStarBitmap(dxSatrs)!!),
                    30, 18, false
                ), 146f, 90f
            )
        }
        if (score.fullComboType != MaimaiEnums.FullComboType.NULL) {
            canvas.drawImage(
                Bitmap.createScaledBitmap(
                    application.getBitmapFromDrawable(score.fullComboType.b50ImageId!!),
                    47, 24, false
                ), 178f, 86f
            )
        }

        if (score.syncType != MaimaiEnums.SyncType.NULL) {
            canvas.drawImage(
                Bitmap.createScaledBitmap(
                    application.getBitmapFromDrawable(score.syncType.b50ImageId!!),
                    47, 24, false
                ), 225f, 86f
            )
        }

        return resultBitmap
    }

    fun generateBestsImage(
        scores: List<MaimaiScoreEntity>
    ): Bitmap {
        val startTime = System.currentTimeMillis()

        val config = application.configManager.config

        val b35Score = scores.filter { it.version < 24000 }
            .sortedByDescending { it.rating }
            .take(35)

        val b15Score = scores.filter { it.version >= 24000 }
            .sortedByDescending { it.rating }
            .take(15)

        val dxRating = getDxRating(b35Score, b15Score)

        val bgBitmap = Bitmap.createScaledBitmap(
            application.getBitmapFromDrawable(R.drawable.ic_maimai_b50_bg), 1440, 1950, false)

        val b50cacheDir = application.filesDir.resolve("b50cache")
        b50cacheDir.mkdirs()
        val outputStream = b50cacheDir.resolve("maimai_b50_$startTime.jpg").outputStream()

        val resultBitmap = Bitmap.createBitmap(
            bgBitmap.width,
            bgBitmap.height,
            bgBitmap.config
        )

        val canvas = Canvas(resultBitmap)

        canvas.drawImage(bgBitmap, 0f, 0f)

        // plate
        val plateBitmap = runBlocking {
            Bitmap.createScaledBitmap(
                NetworkImageRequestUtil.getImageBitmap(
                    "https://assets2.lxns.net/maimai/plate/${config.userInfo.maimaiPlate}.png"
                ) ?: application.getBitmapFromDrawable(R.drawable.ic_maimai_plate_default),
                960, 155, false
            )
        }

        // icon
        val iconBitmap = runBlocking {
            Bitmap.createScaledBitmap(
                NetworkImageRequestUtil.getImageBitmap(
                    "https://assets2.lxns.net/maimai/icon/${config.userInfo.maimaiIcon}.png"
                ) ?: application.getBitmapFromDrawable(R.drawable.ic_maimai_icon_default),
                131, 131, false
            )
        }

        // Rating Frame
        val ratingFrameBitmap = getRatingFrame(dxRating)

        // Rating Num
        val dxRatingNumIcoList = getDxRatingNumIcoList(dxRating)

        // class
        val classBitmap = runBlocking {
            Bitmap.createScaledBitmap(
                application.getImageFromAssets("maimai/class/${config.userInfo.maimaiClass}.png")
                    ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8),
                120, 72, false
            )
        }

        // playerName
        val playerNameBitmap = runBlocking {
            Bitmap.createScaledBitmap(
                application.getBitmapFromDrawable(R.drawable.ic_maimai_playername),
                386, 78, false
            )
        }

        val danBitmap = getDanImage(config.userInfo.maimaiDan)

        val shougouBitmap = getShougouBitmap(config.userInfo.shougouColor)

        // Rating Table
        val ratingTable = runBlocking {
            Bitmap.createScaledBitmap(
                application.getBitmapFromDrawable(R.drawable.ic_maimai_ratingtable),
                814, 325, false
            )
        }

        canvas.drawImage(plateBitmap, 40f, 40f)
        canvas.drawImage(iconBitmap, 52f, 52f)
        canvas.drawImage(ratingFrameBitmap, 190f, 50f)
        dxRatingNumIcoList.forEachIndexed { index, bitmap ->
            canvas.drawImage(bitmap, ratingNumXPosList[index], 62f)
        }
        canvas.drawImage(classBitmap, 430f, 24f)
        canvas.drawImage(playerNameBitmap, 178f, 87f)
        canvas.drawText(
            config.userInfo.name,
            28f, 203f, 134f, 160,
            R.font.source_han_sans_37,
            Color.BLACK,
        )
        canvas.drawImage(danBitmap, 437f, 98f)
        canvas.drawImage(shougouBitmap, 185f, 145f)
        canvas.drawText(
            config.userInfo.shougou,
            16f, 369f, 176f, 336,
            R.font.source_han_sans_37,
            Color.WHITE,
            TextOutline(Color.BLACK, 2f),
            TextAlign.Center
        )
        canvas.drawImage(ratingTable, 40f, 240f)

        val tableTextColor = Color.rgb(30, 54, 99)

        val b15TableValue = getScoresTableValue(b15Score)
        canvas.drawText(
            b15TableValue.avgRating,
            19f, 520f, 323f,
            font = R.font.fot_b,
            color = tableTextColor,
        )
        canvas.drawText(
            b15TableValue.sumRating,
            19f, 650f, 323f,
            font = R.font.fot_b,
            color = tableTextColor,
        )
        canvas.drawText(
            b15TableValue.maxRating,
            24f, 246f, 362f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.minRating,
            24f, 246f, 413f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.ssspMaxLevel,
            24f, 380f, 362f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.ssspMinLevel,
            24f, 380f, 413f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.sssMaxLevel,
            24f, 514f, 362f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.sssMinLevel,
            24f, 514f, 413f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.sspMaxLevel,
            24f, 648f, 362f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.sspMinLevel,
            24f, 648f, 413f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.ssMaxLevel,
            24f, 782f, 362f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b15TableValue.ssMinLevel,
            24f, 782f, 413f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        val b35TableValue = getScoresTableValue(b35Score)
        canvas.drawText(
            b35TableValue.avgRating,
            19f, 520f, 453f,
            font = R.font.fot_b,
            color = tableTextColor,
        )
        canvas.drawText(
            b35TableValue.sumRating,
            19f, 650f, 453f,
            font = R.font.fot_b,
            color = tableTextColor,
        )
        canvas.drawText(
            b35TableValue.maxRating,
            24f, 246f, 492f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.minRating,
            24f, 246f, 542f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.ssspMaxLevel,
            24f, 380f, 492f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.ssspMinLevel,
            24f, 380f, 542f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.sssMaxLevel,
            24f, 514f, 492f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.sssMinLevel,
            24f, 514f, 542f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.sspMaxLevel,
            24f, 648f, 492f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.sspMinLevel,
            24f, 648f, 542f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.ssMaxLevel,
            24f, 782f, 492f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )
        canvas.drawText(
            b35TableValue.ssMinLevel,
            24f, 782f, 542f,
            font = R.font.fot_b,
            color = tableTextColor,
            align = TextAlign.Center,
        )

        val drawJobs = mutableListOf<Job>()

        var lineIndex = 0
        var drawY = 624f
        b15Score.forEachIndexed { index, score ->
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
        drawY = 1034f
        b35Score.forEachIndexed { index, score ->
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

        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        outputStream.close()
        val endTime = System.currentTimeMillis()
        Log.d("MaimaiB50GenerateUtil", "generateMaimaiB50Bitmap: ${(endTime - startTime) / 1000}s")
        BestsImageGenerateViewModel.canGenerate = true
        return resultBitmap
    }
}