package io.github.skydynamic.maiproberplus.core.data.maimai

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MaimaiEnums {
    @Serializable
    enum class SongType(val type: String, val type2: String) {
        @SerialName("standard") STANDARD("standard", "SD"),
        @SerialName("dx") DX("dx", "DX"),
        @SerialName("utage") UTAGE("utage", "UTAGE");

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
        D("d", 0.0000..49.9999),
        C("c", 50.0000..59.9999),
        B("b", 60.0000..69.9999),
        BB("bb", 70.0000..74.9999),
        BBB("bbb", 75.0000..79.9999),
        A("a", 80.0000..89.9999),
        AA("aa", 90.0000..93.9999),
        AAA("aaa", 94.0000..96.9999),
        S("s", 97.0000..97.9999),
        SP("sp", 98.0000..98.9999),
        SS("ss", 99.0000..99.4999),
        SSP("ssp", 99.5000..99.9999),
        SSS("sss", 100.0000..100.4999),
        SSSP("sssp", 100.5000..101.0000);

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
    enum class FullComboType(val typeName: String) {
        @SerialName("")
        NULL(""),
        FC("fc"),
        FCP("fcp"),
        AP("ap"),
        APP("app");

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
    enum class SyncType(val syncName: String) {
        @SerialName("")
        NULL(""),
        SYNC("sync"),
        FS("fs"),
        FSP("fsp"),
        FDX("fsd"),
        FDXP("fsdp");

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