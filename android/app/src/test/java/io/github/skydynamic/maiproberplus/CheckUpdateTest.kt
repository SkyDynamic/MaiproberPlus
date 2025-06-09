package io.github.skydynamic.maiproberplus

import io.github.skydynamic.maiproberplus.core.utils.checkFullUpdate
import io.github.skydynamic.maiproberplus.core.utils.checkReleaseUpdate
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CheckUpdateTest {
    @Test
    fun testCompareVersion() = runBlocking {
        val version = "v1.2.2-c"
        val relese = checkReleaseUpdate(version)
        if (relese != null) {
            println("Need update: ${relese.assets.first().url}")
        } else {
            println("Not update")
        }
    }
}