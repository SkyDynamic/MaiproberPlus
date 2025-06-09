package io.github.skydynamic.maiproberplus.ui.compose.scores.chuni

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.skydynamic.maiproberplus.ui.compose.scores.ScoreManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChuniScoreSortByDialog(
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp)
    ) {
        Card {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    "排序方式",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                ChuniScoreSortBy.entries.forEach {
                    Card(
                        onClick = {
                            ScoreManagerViewModel.chuniScoreSortBy.value = it
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(it.displayName)
                            RadioButton(
                                selected = ScoreManagerViewModel.chuniScoreSortBy.value == it,
                                onClick = null,
                            )
                        }
                    }
                    HorizontalDivider()
                }
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("确定")
                }
            }
        }
    }
}
