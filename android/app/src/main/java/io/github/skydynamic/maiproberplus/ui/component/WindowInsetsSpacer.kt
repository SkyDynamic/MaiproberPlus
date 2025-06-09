package io.github.skydynamic.maiproberplus.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.skydynamic.maiproberplus.Application.Companion.application
import io.github.skydynamic.maiproberplus.GlobalViewModel

object WindowInsetsSpacer {
    val topPadding: Dp
        get() = GlobalViewModel.windowInsetsPadding
            .calculateTopPadding()

    val bottomPadding: Dp
        get() = GlobalViewModel.windowInsetsPadding
            .calculateBottomPadding()

    val startPadding: Dp
        @Composable
        get() = GlobalViewModel.windowInsetsPadding
            .calculateStartPadding(
                application.layoutDirection
            )

    val endPadding: Dp
        @Composable
        get() = GlobalViewModel.windowInsetsPadding
            .calculateEndPadding(
                application.layoutDirection
            )

    @Composable
    fun StartPaddingSpacer(
        modifier: Modifier = Modifier
    ) {
        Spacer(
            modifier.width(startPadding)
        )
    }

    @Composable
    fun EndPaddingSpacer(
        modifier: Modifier = Modifier
    ) {
        Spacer(
            modifier.width(endPadding)
        )
    }

    @Composable
    fun TopPaddingSpacer(
        modifier: Modifier = Modifier
    ) {
        Spacer(
            modifier.height(topPadding)
        )
    }

    @Composable
    fun BottomPaddingSpacer(
        modifier: Modifier = Modifier
    ) {
        Spacer(
            modifier.height(bottomPadding)
        )
    }
}
