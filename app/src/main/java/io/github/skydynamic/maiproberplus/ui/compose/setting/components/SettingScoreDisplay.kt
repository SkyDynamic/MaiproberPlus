package io.github.skydynamic.maiproberplus.ui.compose.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ScoreDisplayExampleSmall(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                MaterialTheme.shapes.small,
            )
            .aspectRatio(1f)
    ) {
        Row(
            Modifier.fillMaxSize().padding(4.dp),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically,
        ) {
            for (i in 1..2) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (j in 1..4) {
                        Box(
                            Modifier
                                .aspectRatio(2f)
                                .padding(4.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primary)
                                .weight(1f)
                        ) {}
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ScoreDisplayExampleMiddle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                MaterialTheme.shapes.small,
            )
            .aspectRatio(1f)
    ) {
        Row(
            Modifier.fillMaxSize().padding(4.dp),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically,
        ) {
            for (i in 1..2) {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (j in 1..2) {
                        Box(
                            Modifier
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primary)
                                .weight(1f)
                        ) {}
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ScoreDisplayExampleLarge(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                MaterialTheme.shapes.small,
            )
            .aspectRatio(1f)
    ) {
        Column(
            Modifier.fillMaxSize().padding(4.dp),
            Arrangement.SpaceEvenly,
            Alignment.CenterHorizontally,
        ) {
            for (i in 1..2) {
                Box(
                    Modifier
                        .aspectRatio(2f)
                        .padding(4.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                        .weight(1f)
                ) {}
            }
        }
    }
}
