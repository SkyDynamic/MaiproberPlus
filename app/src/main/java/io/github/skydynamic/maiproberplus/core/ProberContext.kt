package io.github.skydynamic.maiproberplus.core

import io.github.skydynamic.maiproberplus.core.config.ConfigStorage

interface ProberContext {
    fun requireConfig(): ConfigStorage
}