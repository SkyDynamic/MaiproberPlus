package io.github.skydynamic.maiproberplus.core.data.maimai

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.skydynamic.maiproberplus.R

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
        val imageId: Int
    ) {
        D("D", 0.0000..49.9999, R.drawable.ic_maimai_d),
        C("C", 50.0000..59.9999, R.drawable.ic_maimai_c),
        B("B", 60.0000..69.9999, R.drawable.ic_maimai_b),
        BB("BB", 70.0000..74.9999, R.drawable.ic_maimai_bb),
        BBB("BBB", 75.0000..79.9999, R.drawable.ic_maimai_bbb),
        A("A", 80.0000..89.9999, R.drawable.ic_maimai_a),
        AA("AA", 90.0000..93.9999, R.drawable.ic_maimai_aa),
        AAA("AAA", 94.0000..96.9999, R.drawable.ic_maimai_aaa),
        S("S", 97.0000..97.9999, R.drawable.ic_maimai_s),
        SP("Sp", 98.0000..98.9999, R.drawable.ic_maimai_sp),
        SS("SS", 99.0000..99.4999, R.drawable.ic_maimai_ss),
        SSP("SSp", 99.5000..99.9999, R.drawable.ic_maimai_ssp),
        SSS("SSS", 100.0000..100.4999, R.drawable.ic_maimai_sss),
        SSSP("SSSp", 100.5000..101.0000, R.drawable.ic_maimai_sssp);

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
    enum class FullComboType(val typeName: String, val typeName2: String, val imageId: Int) {
        @SerialName("") NULL("", "无", R.drawable.ic_maimai_back),
        FC("fc", "FC", R.drawable.ic_maimai_fc),
        FCP("fcp", "FC+", R.drawable.ic_maimai_fcp),
        AP("ap", "AP", R.drawable.ic_maimai_ap),
        APP("app", "AP+", R.drawable.ic_maimai_app);

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
    enum class SyncType(val syncName: String, val typeName2: String, val imageId: Int) {
        @SerialName("") NULL("", "无", R.drawable.ic_maimai_back),
        SYNC("sync", "SYNC", R.drawable.ic_maimai_sync),
        FS("fs", "FS", R.drawable.ic_maimai_fs),
        FSP("fsp", "FS+", R.drawable.ic_maimai_fsp),
        FDX("fsd", "FDX", R.drawable.ic_maimai_fsd),
        FDXP("fsdp", "FDX+", R.drawable.ic_maimai_fsdp);

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