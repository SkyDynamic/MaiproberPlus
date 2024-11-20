package io.github.skydynamic.maiproberplus.core.config

import android.content.Context
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import io.github.skydynamic.maiproberplus.ui.compose.application
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
)

@Serializable
data class MaimaiProberUserInfo(
    var name: String = "",
    var maimaiDan: Int = 0,
    var maimaiIcon: Int = 1,
    var maimaiPlate: Int = 1,
    var maimaiTitle: String = ""
)