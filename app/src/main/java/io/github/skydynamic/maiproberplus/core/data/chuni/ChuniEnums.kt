package io.github.skydynamic.maiproberplus.core.data.chuni

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

class ChuniEnums {
    @Serializable
    enum class Difficulty(
        val diffName: String,
        val diffIndex: Int,
        val color: Color
    ) {
        BASIC("Basic", 0, Color(28, 133, 0)),
        ADVANCED("Advanced", 1, Color(168, 137, 0, 255)),
        EXPERT("Expert", 2, Color(220, 40, 40)),
        MASTER("Master", 3, Color(165, 0, 235)),
        ULTIMA("Ultima", 4, Color(33, 29, 29, 255)),
        WORLDSEND("World's End", 0, Color.Magenta),
        RECENT("Recent", 6, Color.White);

        companion object {
            @JvmStatic
            fun getDifficultyWithName(diffName: String): Difficulty {
                for (difficulty in entries) {
                    if (difficulty.diffName.lowercase() == diffName.lowercase()) {
                        return difficulty
                    }
                }
                throw IllegalArgumentException("No such difficulty")
            }

            @JvmStatic
            fun getDifficultyWithIndex(diffIndex: Int): Difficulty {
                for (difficulty in entries) {
                    if (difficulty.ordinal == diffIndex) {
                        return difficulty
                    }
                }
                throw IllegalArgumentException("No such difficulty")
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
                for (type in ClearType.entries) {
                    if (type.type.lowercase() == typeName.lowercase()) {
                        return type
                    }
                }
                return FAILED
            }
        }
    }

    @Serializable
    enum class FullComboType(val type: String) {
        NULL(""),
        AJC("alljusticecritical"),
        AJ("alljustice"),
        FC("fullcombo");

        companion object {
            @JvmStatic
            fun getFullComboTypeWithName(typeName: String): FullComboType {
                for (type in FullComboType.entries) {
                    if (type.type.lowercase() == typeName.lowercase()) {
                        return type
                    }
                }
                return NULL
            }
        }
    }

    @Serializable
    enum class FullChainType(val type: String) {
        NULL(""),
        FC("fullchain"),
        GFC("fullchain2");

        companion object {
            @JvmStatic
            fun getFullChainTypeWithName(typeName: String): FullChainType {
                for (type in FullChainType.entries) {
                    if (type.type.lowercase() == typeName.lowercase()) {
                        return type
                    }
                }
                return NULL
            }
        }
    }

    @Serializable
    enum class RankType(
        val rank: String,
        private val intRange: IntRange,
    ) {
        D("d", 0..499999),
        C("c", 500000..599999),
        B("b", 600000..699999),
        BB("bb", 700000..799999),
        BBB("bbb", 800000..899999),
        A("a", 900000..924999),
        AA("aa", 925000..949999),
        AAA("aaa", 950000..974999),
        S("s", 975000..989999),
        SP("sp", 990000..999999),
        SS("ss", 1000000..1004999),
        SSP("ssp", 1005000..1007499),
        SSS("sss", 1007500..1008999),
        SSSP("sssp", 1009000..1010000);

        companion object {
            @JvmStatic
            fun getRankTypeByScore(score: Int): RankType {
                var returnValue = D
                for (rank in entries) {
                    if (score in rank.intRange) {
                        returnValue = rank
                    }
                }
                return returnValue
            }
        }
    }
}