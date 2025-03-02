package io.github.skydynamic.maiproberplus

import io.github.skydynamic.maiproberplus.core.utils.checkUpdate
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CheckUpdateTest {
    @Test
    fun testCompareVersion() = runBlocking {
        val version = "v1.2.1-c"
        val relese = checkUpdate(version)
        if (relese != null) {
            println("Need update: ${relese.assets.first().url}")
        } else {
            println("Not update")
        }
    }
}