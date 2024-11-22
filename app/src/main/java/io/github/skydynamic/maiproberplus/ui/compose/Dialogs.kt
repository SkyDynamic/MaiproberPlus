package io.github.skydynamic.maiproberplus.ui.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.skydynamic.maiproberplus.ui.theme.getCardColor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.io.files.FileMetadata
import java.io.File

val httpClient = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 30000
        connectTimeoutMillis = 30000
    }
}

@Composable
fun InfoDialog(info: String, onRequest: () -> Unit) {
    Dialog(onDismissRequest = { onRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = info,
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("确认")
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DownloadDialog(
    downloadFileMetas: List<FileDownloadMeta>,
    onRequest: () -> Unit
) {
    var finish by remember { mutableFloatStateOf(0F) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(downloadFileMetas) {
        val totalFiles = downloadFileMetas.size
        var completedFiles = 0
        for (meta in downloadFileMetas) {
            val job = scope.async(Dispatchers.IO) {
                val response = httpClient.get(meta.fileDownloadUrl)
                if (response.status.value == 200) {
                    File(application.filesDir, meta.fileName).deleteOnExit()
                    val outputStream = application.getFilesDirOutputStream(meta.fileName)
                    outputStream.bufferedWriter().use {
                        it.write(response.bodyAsText())
                    }
                    completedFiles++
                    finish = completedFiles.toFloat() / totalFiles
                }
            }

            job.await()
        }
        onRequest()
    }

    BasicAlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = {},
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LinearProgressIndicator(
                    progress = { finish },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${(finish * 100).toInt()}%")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DiffChooseDialog(
    onRequest: (List<Int>) -> Unit,
    defaultList: List<String>,
    currentChoiceList: List<Int>,
    onDismissRequest: () -> Unit
) {
    val currentChoiceDifficulties = remember {
        mutableStateListOf(*currentChoiceList.toTypedArray()) }

    BasicAlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = getCardColor())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("选择要爬取的难度")

                defaultList.forEachIndexed { index, item ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item)
                        Checkbox(
                            checked = index in currentChoiceDifficulties,
                            onCheckedChange = {
                                if (index !in currentChoiceDifficulties) {
                                    currentChoiceDifficulties.add(index)
                                } else {
                                    currentChoiceDifficulties.remove(index)
                                }
                            }
                        )
                    }
                }
            }
            Row {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        onRequest(currentChoiceDifficulties)
                        onDismissRequest()
                    }
                ) {
                    Text("确认")
                }

                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text("取消")
                }
            }
        }
    }
}
