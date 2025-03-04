package io.github.skydynamic.maiproberplus.core.utils

import android.util.Log
import io.github.skydynamic.maiproberplus.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.OffsetDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern

class VersionBody(
    val major: Int = 0,
    val minor: Int = 0,
    val patch: Int = 0,
    val gitHash: String = "",
) {
    override fun toString(): String {
        return "$major.$minor.$patch" + if(gitHash.isNotEmpty()) "-${gitHash}" else ""
    }
}

@Serializable
data class Release(
    @SerialName("tag_name") val tagName: String,
    val prerelease: Boolean,
    @SerialName("created_at") val createdAt: String = "",
    val assets: List<Asset> = emptyList(),
    val body: String = ""
) {
    @Serializable
    data class Asset(
        val name: String,
        val size: Long,
        @SerialName("browser_download_url") val url: String
    )
}

@Serializable
data class Commit(
    val sha: String,
    val commit: Commit
) {
    @Serializable
    data class Commit(
        val committer: GitCommitUser
    )

    @Serializable
    data class GitCommitUser(
        val name: String,
        val date: String
    )
}

private fun formatVersionName(versionName: String): VersionBody {
    val pattern: Pattern = Pattern.compile("(v?)(\\d+)\\.(\\d+)\\.(\\d+)(?:-([a-zA-Z0-9]+))?")
    val matcher: Matcher = pattern.matcher(versionName)

    if (matcher.matches()) {
        val major = matcher.group(2)?.toInt()
        val minor = matcher.group(3)?.toInt()
        val patch = matcher.group(4)?.toInt()
        val gitHash = matcher.group(5)

        return VersionBody(major!!, minor!!, patch!!, gitHash ?: "")
    } else {
        throw IllegalArgumentException("Invalid version name format: $versionName")
    }
}

private fun compareVersion(a: VersionBody, b: VersionBody): Int {
    // 1 -> a is newer
    // 0 -> same version
    // -1 -> b is newer
    // 2 -> version is same but b is release and a is snapshot
    val r = when {
        a.major > b.major -> 1
        a.major < b.major -> -1
        a.minor > b.minor -> 1
        a.minor < b.minor -> -1
        a.patch > b.patch -> 1
        a.patch < b.patch -> -1
        else -> 0
    }
    if (r == 0 && b.gitHash == "" && a.gitHash != "") return 2
    if (r == 0 && a.gitHash == "") return 1
    return r
}

private suspend fun getLatestReleaseFromGitHub(): Release? {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    try {
        val response: HttpResponse =
            client.get("https://api.github.com/repos/SkyDynamic/MaiproberPlus/releases")
        val tags: List<Release> = response.body()
        return tags.firstOrNull()
    } catch (e: Exception) {
        Log.e("CheckUpdateUtils", "Failed to fetch tags: ${e.message}")
    } finally {
        client.close()
    }
    return null
}

private suspend fun getCommitsFromGitHub(): List<Commit>? {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    try {
        val response: HttpResponse =
            client.get("https://api.github.com/repos/SkyDynamic/MaiproberPlus/commits")
        val commits: List<Commit> = response.body()
        return commits
    } catch (e: Exception) {
        Log.e("CheckUpdateUtils", "Failed to fetch commits: ${e.message}")
    } finally {
        client.close()
    }
    return null
}

private fun parseDateToTimestamp(dateString: String): Long {
    return try {
        val offsetDateTime = OffsetDateTime.parse(dateString)
        offsetDateTime.toInstant().toEpochMilli()
    } catch (e: Exception) {
        Log.e("CheckUpdateUtils", "Failed to parse date: ${e.message}")
        0L
    }
}

suspend fun checkUpdate(versionName: String = BuildConfig.VERSION_NAME): Release? {
    val localVersionBody = formatVersionName(versionName)

    val latestRelease = getLatestReleaseFromGitHub()
    if (latestRelease == null) return null
    val latestVersionBody = formatVersionName(latestRelease.tagName)

    return when(compareVersion(localVersionBody, latestVersionBody)) {
        0 -> {
            val commits = getCommitsFromGitHub()
            if (commits != null && commits.isNotEmpty()) {
                val remoteCommit = commits.find { it.sha.contains(localVersionBody.gitHash) }
                remoteCommit?.let {
                    val remoteCommitDate = parseDateToTimestamp(it.commit.committer.date)
                    val latestTagCommit = commits.find { it.sha.contains(latestVersionBody.gitHash) }
                    latestTagCommit?.let {
                        val latestCommitDate = parseDateToTimestamp(it.commit.committer.date)
                        return if (remoteCommitDate < latestCommitDate) latestRelease else null
                    }
                    return null
                }
                return null
            }
            return null
        }
        1 -> null
        2 -> {
            val buildType = BuildConfig.BUILD_TYPE
            return if (!latestRelease.prerelease) {
                if (buildType != "release") return latestRelease
                else null
            } else {
                null
            }
        }
        else -> latestRelease
    }
}