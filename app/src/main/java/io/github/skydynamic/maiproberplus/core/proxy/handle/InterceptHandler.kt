package io.github.skydynamic.maiproberplus.core.proxy.handle

import io.github.skydynamic.maiproberplus.GlobalViewModel
import io.github.skydynamic.maiproberplus.core.config.ConfigStorage
import io.github.skydynamic.maiproberplus.core.prober.ProberPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object InterceptHandler {
    @JvmStatic
    @OptIn(DelicateCoroutinesApi::class)
    fun onAuthHook(authUrl: String, config: ConfigStorage) {
        val target = authUrl.replace("http", "https")
        val currentPlatform = ProberPlatform.entries[GlobalViewModel.platformIndex]
        val proberUtil = currentPlatform.factory
        var token: String = ""
        if (currentPlatform == ProberPlatform.LXNS) {
            token = config.lxnsToken
        } else if(currentPlatform == ProberPlatform.DIVING_FISH) {
            token = config.divingfishToken
        }

        if (!token.isEmpty()) {
            if (target.contains("maimai-dx")) {
                GlobalScope.launch(Dispatchers.IO) {
                    proberUtil.uploadMaimaiProberData(token, target)
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                proberUtil.sendMessageToUi("token为空")
            }
        }
    }
}