package io.github.skydynamic.maiproberplus.core.data.maimai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MaimaiEnums {
    @Serializable
    enum class SongType(val type: String) {
        @SerialName("standard") STANDARD("standard"),
        @SerialName("dx") DX("dx"),
        @SerialName("utage") UTAGE("utage");
    }

    @Serializable
    enum class Difficulty(val diffName: String, val diffIndex: Int) {
        BASIC("Basic", 0),
        ADVANCED("Advanced", 1),
        EXPERT("Expert", 2),
        MASTER("Master", 3),
        REMASTER("Re:Master", 4);

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
    enum class ClearType(
        val clearName: String,
        private val closedFloatingPointRange: ClosedFloatingPointRange<Double>,
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
            fun getClearTypeByScore(score: Float): ClearType {
                var returnValue = D
                for (clearType in entries) {
                    if (score in clearType.closedFloatingPointRange) {
                        returnValue = clearType
                    }
                }
                return returnValue
            }
        }
    }

    @Serializable
    enum class SpecialClearType(val sepcialClearName: String) {
        @SerialName("")
        NULL(""),
        FC("fc"),
        FCP("fcp"),
        AP("ap"),
        APP("app");

        companion object {
            @JvmStatic
            fun getSpecialClearType(specialClearName: String): SpecialClearType {
                for (specialClearType in entries) {
                    if (specialClearType.sepcialClearName == specialClearName.lowercase()) {
                        return specialClearType
                    }
                }
                throw IllegalArgumentException("No such special clear type")
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
            fun getSyncType(syncName: String): SyncType {
                for (syncType in entries) {
                    if (syncType.syncName == syncName.lowercase()) {
                        return syncType
                    }
                }
                return NULL
            }
        }
    }
}