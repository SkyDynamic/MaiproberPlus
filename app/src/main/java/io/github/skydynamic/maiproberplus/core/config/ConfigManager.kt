package io.github.skydynamic.maiproberplus.core.config

import android.content.Context
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.core.data.chuni.ChuniEnums
import io.github.skydynamic.maiproberplus.core.data.maimai.MaimaiEnums
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File

val JSON = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@OptIn(ExperimentalSerializationApi::class)
open class ConfigManager(context: Context) {
    private var configFile: File = File(context.filesDir, "config.json")

    var config: ConfigStorage = ConfigStorage()

    init {
        if (!configFile.exists()) {
            configFile.createNewFile()
            this.save()
        } else {
            this.read()
        }
    }

    private fun read() {
        val configInputStream = application.getFilesDirInputStream("config.json")
        config = JSON.decodeFromStream(configInputStream)
        configInputStream.close()
    }

    fun save() {
        val configOutputStream = application.getFilesDirOutputStream("config.json")
        JSON.encodeToStream(config, configOutputStream)
        configOutputStream.close()
    }
}

@Serializable
enum class ScoreDisplayType(val displayName: String) {
    Small("小"),
    Middle("中"),
    Large("大"),
}

@Serializable
enum class ScoreStyleType(val displayName: String) {
    ColorOverlay("颜色覆盖"),
    TextShadow("文本阴影"),
}

@Serializable
data class ConfigStorage(
    var divingfishToken: String = "",
    var lxnsToken: String = "",
    var syncConfig: SyncConfig = SyncConfig(),
    var localConfig: LocalConfig = LocalConfig(),
    var userInfo: UserInfo = UserInfo(),
    var scoreDisplayType: ScoreDisplayType = ScoreDisplayType.Small,
    var scoreStyleType: ScoreStyleType = ScoreStyleType.ColorOverlay,
)

@Serializable
data class SyncConfig(
    var maimaiSyncDifficulty: List<Int> = MaimaiEnums.Difficulty.entries.map { it.diffIndex },
    var chuniSyncDifficulty: List<Int> = ChuniEnums.Difficulty.entries.map { it.diffIndex }
)

@Serializable
data class LocalConfig(
    var checkUpdate: Boolean = true,
    var cacheScore: Boolean = false,
    var parseMaimaiUserInfo: Boolean = false
)

@Serializable
data class UserInfo(
    var name: String = "MaiProberPlus",
    var maimaiDan: Int = 0,
    var maimaiIcon: Int = 1,
    var maimaiPlate: Int = 1,
    var maimaiClass: Int = 0,
    val chuniCharacter: Int = 0,
    var shougou: String = "Generate by MaiProberPlus",
    var shougouColor: String = "normal",
)