package io.github.skydynamic.maiproberplus.core.data.maimai

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import io.github.skydynamic.maiproberplus.Application
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MaimaiEnums {
    @Serializable
    enum class SongType(val type: String, val type2: String) {
        @SerialName("standard") STANDARD("standard", "SD"),
        @SerialName("dx") DX("dx", "DX");

        companion object {
            @JvmStatic
            fun getSongTypeByName(typeName: String): SongType {
                for (songType in SongType.entries) {
                    if (typeName == songType.type || typeName == songType.type2) {
                        return songType
                    }
                }
                return STANDARD
            }
        }
    }

    @Serializable
    enum class Difficulty(val diffName: String, val diffIndex: Int, val color: Color) {
        BASIC("Basic", 0, Color(28, 133, 0)),
        ADVANCED("Advanced", 1, Color(168, 137, 0, 255)),
        EXPERT("Expert", 2, Color(220, 40, 40)),
        MASTER("Master", 3, Color(165, 0, 235)),
        REMASTER("Re:Master", 4, Color(186, 153, 255));

        companion object {
            @JvmStatic
            fun getDifficultyWithIndex(diffIndex: Int): Difficulty {
                for (difficulty in entries) {
                    if (difficulty.diffIndex == diffIndex) {
                        return difficulty
                    }
                }
                throw IllegalArgumentException("No such difficulty")
            }
        }
    }

    @Serializable
    enum class RankType(
        val rank: String,
        private val scoreRange: ClosedFloatingPointRange<Double>,
    ) {
        D("D", 0.0000..49.9999),
        C("C", 50.0000..59.9999),
        B("B", 60.0000..69.9999),
        BB("BB", 70.0000..74.9999),
        BBB("BBB", 75.0000..79.9999),
        A("A", 80.0000..89.9999),
        AA("AA", 90.0000..93.9999),
        AAA("AAA", 94.0000..96.9999),
        S("S", 97.0000..97.9999),
        SP("Sp", 98.0000..98.9999),
        SS("SS", 99.0000..99.4999),
        SSP("SSp", 99.5000..99.9999),
        SSS("SSS", 100.0000..100.4999),
        SSSP("SSSp", 100.5000..101.0000);

        fun getIcoBitmap(): Bitmap? {
            return Application.application.assetsManager.getMaimaiUIAssets(
                "UI_TTR_Rank_${rank}.png"
            )
        }

        companion object {
            @JvmStatic
            fun getRankTypeByScore(score: Float): RankType {
                var returnValue = D
                for (rank in entries) {
                    if (score in rank.scoreRange) {
                        returnValue = rank
                    }
                }
                return returnValue
            }
        }
    }

    @Serializable
    enum class FullComboType(val typeName: String, val typeName2: String, val icoFileName: String) {
        @SerialName("") NULL("", "无", "UI_CHR_PlayBonus_Back.png"),
        FC("fc", "FC", "UI_CHR_PlayBonus_FC.png"),
        FCP("fcp", "FC+", "UI_CHR_PlayBonus_FCp.png"),
        AP("ap", "AP", "UI_CHR_PlayBonus_AP.png"),
        APP("app", "AP+", "UI_CHR_PlayBonus_APp.png");

        fun getIcoBitmap(): Bitmap? {
            return Application.application.assetsManager.getMaimaiUIAssets(icoFileName)
        }

        companion object {
            @JvmStatic
            fun getFullComboTypeByName(typeName: String): FullComboType {
                for (fullComboType in FullComboType.entries) {
                    if (typeName == fullComboType.typeName) {
                        return fullComboType
                    }
                }
                return NULL
            }
        }
    }

    @Serializable
    enum class SyncType(val syncName: String, val typeName2: String, val icoFileName: String) {
        @SerialName("") NULL("", "无", "UI_CHR_PlayBonus_Back.png"),
        SYNC("sync", "SYNC", "UI_CHR_PlayBonus_Sync.png"),
        FS("fs", "FS", "UI_CHR_PlayBonus_FS.png"),
        FSP("fsp", "FS+", "UI_CHR_PlayBonus_FSp.png"),
        FDX("fsd", "FDX", "UI_CHR_PlayBonus_FSD.png"),
        FDXP("fsdp", "FDX+", "UI_CHR_PlayBonus_FSDp.png");

        fun getIcoBitmap(): Bitmap? {
            return Application.application.assetsManager.getMaimaiUIAssets(icoFileName)
        }

        companion object {
            @JvmStatic
            fun getSyncTypeByName(syncName: String): SyncType {
                for (syncType in SyncType.entries) {
                    if (syncName == syncType.syncName) {
                        return syncType
                    }
                }
                return NULL
            }
        }
    }
}