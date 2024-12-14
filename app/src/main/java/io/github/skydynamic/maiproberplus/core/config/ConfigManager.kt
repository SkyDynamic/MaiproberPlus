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
data class ConfigStorage(
    var divingfishToken: String = "",
    var lxnsToken: String = "",
    var maimaiPresonalInfo: MaimaiProberUserInfo = MaimaiProberUserInfo(),
    var syncConfig: SyncConfig = SyncConfig(),
    var localConfig: LocalConfig = LocalConfig(),
    var userInfo: UserInfo = UserInfo()
)

@Serializable
data class MaimaiProberUserInfo(
    var name: String = "",
    var maimaiDan: Int = 0,
    var maimaiIcon: Int = 1,
    var maimaiPlate: Int = 1,
    var maimaiTitle: String = ""
)

@Serializable
data class SyncConfig(
    var maimaiSyncDifficulty: List<Int> = MaimaiEnums.Difficulty.entries.map { it.diffIndex },
    var chuniSyncDifficulty: List<Int> = ChuniEnums.Difficulty.entries.map { it.diffIndex }
)

@Serializable
data class LocalConfig(
    var cacheScore: Boolean = false,
    var parseMaimaiUserInfo: Boolean = false
)

@Serializable
data class UserInfo(
    var name: String = "maimaiDX",
    var maimaiDan: Int = 0,
    var maimaiIcon: Int = 1,
    var maimaiPlate: Int = 1,
    var maimaiClass: Int = 0,
    var shougou: String = "Welcome to maimaiDX",
    var shougouColor: String = "normal"
)