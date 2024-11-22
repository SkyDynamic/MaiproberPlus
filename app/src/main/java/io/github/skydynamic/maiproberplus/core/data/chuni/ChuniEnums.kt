package io.github.skydynamic.maiproberplus.core.data.chuni

import kotlinx.serialization.Serializable

class ChuniEnums {
    @Serializable
    enum class Difficulty(val diffName: String, val diffIndex: Int) {
        BASIC("Basic", 0),
        ADVANCED("Advanced", 1),
        EXPERT("Expert", 2),
        MASTER("Master", 3),
        ULTIMA("Ultima", 4),
        WORLDSEND("World's End", 0),
        RECENT("Recent", 6);

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
    }

    @Serializable
    enum class FullComboType(val type: String) {
        NULL(""),
        AJC("alljusticecritical"),
        AJ("alljustice"),
        FC("fullcombo");
    }

    @Serializable
    enum class FullChainType(val type: String) {
        NULL(""),
        FC("fullchain"),
        GFC("fullchain2")
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