package io.github.skydynamic.maiproberplus.core.data.chuni

import androidx.compose.ui.graphics.Color
import io.github.skydynamic.maiproberplus.R
import kotlinx.serialization.Serializable

object ChuniEnums {
    @Serializable
    enum class Difficulty(
        val diffName: String,
        val diffIndex: Int,
        val color: Color
    ) {
        BASIC("Basic", 0, Color(28, 133, 0)),
        ADVANCED("Advanced", 1, Color(168, 137, 0)),
        EXPERT("Expert", 2, Color(220, 40, 40)),
        MASTER("Master", 3, Color(165, 0, 235)),
        ULTIMA("Ultima", 4, Color(33, 29, 29)),
        WORLDSEND("World's End", 0, Color.Magenta),
        RECENT("Recent", 6, Color.White);

        companion object {
            @JvmStatic
            fun getDifficultyWithName(diffName: String): Difficulty {
                return entries.filter { it.diffName.lowercase() == diffName.lowercase() }
                    .getOrElse(0) { BASIC }
            }

            @JvmStatic
            fun getDifficultyWithIndex(diffIndex: Int): Difficulty {
                return entries.filter { it.diffIndex == diffIndex }.getOrElse(0) { BASIC }
            }
        }
    }

    @Serializable
    enum class ClearType(val type: String) {
        CATASTROPHY("catastrophy"),
        ABSOLUTEP("absolutep"),
        ABSOLUTE("absolute"),
        HARD("hard"),
        CLEAR("clear"),
        FAILED("failed");

        companion object {
            @JvmStatic
            fun getClearTypeWithName(typeName: String): ClearType {
                return entries.filter { it.type.lowercase() == typeName.lowercase() }
                    .getOrElse(0) { CLEAR }
            }
        }
    }

    @Serializable
    enum class FullComboType(val typeName: String, val typeName2: String, val imageId: Int = 0) {
        NULL("", "无"),
        AJC("alljusticecritical", "AJC", R.drawable.ic_chuni_ajc),
        AJ("alljustice", "AJ", R.drawable.ic_chuni_aj),
        FC("fullcombo", "FC", R.drawable.ic_chuni_full_combo);

        companion object {
            @JvmStatic
            fun getFullComboTypeWithName(typeName: String): FullComboType {
                return entries.filter { it.typeName.lowercase() == typeName.lowercase() }
                    .getOrElse(0) { NULL }
            }
        }
    }

    @Serializable
    enum class FullChainType(val typeName: String, val typeName2: String, val imageId: Int = 0) {
        NULL("", "无"),
        FC("fullchain", "金", R.drawable.ic_chuni_full_chain_1),
        GFC("fullchain2", "铂", R.drawable.ic_chuni_full_chain_2);

        companion object {
            @JvmStatic
            fun getFullChainTypeWithName(typeName: String): FullChainType {
                return entries.filter { it.typeName.lowercase() == typeName.lowercase() }
                    .getOrElse(0) { NULL }
            }
        }
    }

    @Serializable
    enum class RankType(
        val rank: String,
        private val intRange: IntRange,
        val imageId: Int
    ) {
        D("d", 0..499999, R.drawable.ic_chuni_result_d),
        C("c", 500000..599999, R.drawable.ic_chuni_result_c),
        B("b", 600000..699999, R.drawable.ic_chuni_result_b),
        BB("bb", 700000..799999, R.drawable.ic_chuni_result_bb),
        BBB("bbb", 800000..899999, R.drawable.ic_chuni_result_bbb),
        A("a", 900000..924999, R.drawable.ic_chuni_result_a),
        AA("aa", 925000..949999, R.drawable.ic_chuni_result_aa),
        AAA("aaa", 950000..974999, R.drawable.ic_chuni_result_aaa),
        S("s", 975000..989999, R.drawable.ic_chuni_result_s),
        SP("sp", 990000..999999, R.drawable.ic_chuni_result_sp),
        SS("ss", 1000000..1004999, R.drawable.ic_chuni_result_ss),
        SSP("ssp", 1005000..1007499, R.drawable.ic_chuni_result_ssp),
        SSS("sss", 1007500..1008999, R.drawable.ic_chuni_result_sss),
        SSSP("sssp", 1009000..1010000, R.drawable.ic_chuni_result_sssp);

        companion object {
            @JvmStatic
            fun getRankTypeByScore(score: Int): RankType {
                return entries.filter { score in it.intRange }.getOrElse(0) { D }
            }
        }
    }
}