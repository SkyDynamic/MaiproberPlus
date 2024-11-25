package io.github.skydynamic.maiproberplus.core.prober

enum class ProberPlatform(val proberName: String, val factory: IProberUtil) {
    DIVING_FISH("水鱼查分器", DivingFishProberUtil()),
    LXNS("落雪查分器", LxnsProberUtil()),
    LOCAL("本地", LocalProberUtil())
}