package io.github.skydynamic.maiproberplus.core.data.maimai

import androidx.compose.ui.graphics.Color
import io.github.skydynamic.maiproberplus.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object MaimaiEnums {
    @Serializable
    enum class SongType(val type: String, val type2: String) {
        @SerialName("standard") STANDARD("standard", "SD"),
        @SerialName("dx") DX("dx", "DX"),
        @SerialName("utage") UTAGE("utage", "UTAGE");

        companion object {
            @JvmStatic
            fun getSongTypeByName(typeName: String): SongType {
                return entries.filter { it.type == typeName || it.type2 == typeName }
                    .getOrElse(0) { STANDARD }
            }
        }
    }

    @Serializable
    enum class Difficulty(val diffName: String, val diffIndex: Int, val color: Color) {
        BASIC("Basic", 0, Color(28, 133, 0)),
        ADVANCED("Advanced", 1, Color(168, 137, 0)),
        EXPERT("Expert", 2, Color(220, 40, 40)),
        MASTER("Master", 3, Color(165, 0, 235)),
        REMASTER("Re:Master", 4, Color(186, 153, 255));

        companion object {
            @JvmStatic
            fun getDifficultyWithIndex(diffIndex: Int): Difficulty {
                return entries.filter { it.diffIndex == diffIndex }.getOrElse(0) { BASIC }
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
                return entries.filter { score in it.scoreRange }.getOrElse(0) { D }
            }
        }
    }

    @Serializable
    enum class FullComboType(
        val typeName: String,
        val typeName2: String,
        val imageId: Int,
        val b50ImageId: Int?
    ) {
        @SerialName("") NULL("", "无", R.drawable.ic_maimai_back, null),
        FC("fc", "FC", R.drawable.ic_maimai_fc, R.drawable.ic_maimai_b50_fc),
        FCP("fcp", "FC+", R.drawable.ic_maimai_fcp, R.drawable.ic_maimai_b50_fcp),
        AP("ap", "AP", R.drawable.ic_maimai_ap, R.drawable.ic_maimai_b50_ap),
        APP("app", "AP+", R.drawable.ic_maimai_app, R.drawable.ic_maimai_b50_app);

        companion object {
            @JvmStatic
            fun getFullComboTypeByName(typeName: String): FullComboType {
                return entries.filter { it.typeName == typeName }.getOrElse(0) { NULL }
            }
        }
    }

    @Serializable
    enum class SyncType(
        val syncName: String,
        val typeName2: String,
        val imageId: Int,
        val b50ImageId: Int?
    ) {
        @SerialName("") NULL("", "无", R.drawable.ic_maimai_back, null),
        SYNC("sync", "SYNC", R.drawable.ic_maimai_sync, R.drawable.ic_maimai_b50_sync),
        FS("fs", "FS", R.drawable.ic_maimai_fs, R.drawable.ic_maimai_b50_fs),
        FSP("fsp", "FS+", R.drawable.ic_maimai_fsp, R.drawable.ic_maimai_b50_fsp),
        FDX("fsd", "FDX", R.drawable.ic_maimai_fsd, R.drawable.ic_maimai_b50_fsd),
        FDXP("fsdp", "FDX+", R.drawable.ic_maimai_fsdp, R.drawable.ic_maimai_b50_fsdp);

        companion object {
            @JvmStatic
            fun getSyncTypeByName(syncName: String): SyncType {
                return entries.filter { it.syncName == syncName }.getOrElse(0) { NULL }
            }
        }
    }
}