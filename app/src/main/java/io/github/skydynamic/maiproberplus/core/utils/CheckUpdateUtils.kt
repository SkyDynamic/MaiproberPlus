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

    return try {
        val response: HttpResponse =
            client.get("https://api.github.com/repos/SkyDynamic/MaiproberPlus/releases")
        val tags: List<Release> = response.body()
        tags.sortedByDescending { parseDateToTimestamp(it.createdAt) }.firstOrNull()
    } catch (e: Exception) {
        Log.e("CheckUpdateUtils", "Failed to fetch tags: ${e.message}")
        null
    } finally {
        client.close()
    }
}

private suspend fun getCommitsFromGitHub(): List<Commit>? {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    return try {
        val response: HttpResponse =
            client.get("https://api.github.com/repos/SkyDynamic/MaiproberPlus/commits")
        response.body()
    } catch (e: Exception) {
        Log.e("CheckUpdateUtils", "Failed to fetch commits: ${e.message}")
        null
    } finally {
        client.close()
    }
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

suspend fun checkFullUpdate(versionName: String = BuildConfig.VERSION_NAME): Release? {
    val localVersionBody = formatVersionName(versionName)

    val latestRelease = getLatestReleaseFromGitHub() ?: return null
    val latestVersionBody = formatVersionName(latestRelease.tagName)

    return when(compareVersion(localVersionBody, latestVersionBody)) {
        0 -> {
            val commits = getCommitsFromGitHub() ?: return null
            val remoteCommit = commits.find {
                it.sha.contains(localVersionBody.gitHash)
            } ?: return null
            val latestTagCommit = commits.find {
                it.sha.contains(latestVersionBody.gitHash)
            } ?: return null

            val remoteCommitDate = parseDateToTimestamp(remoteCommit.commit.committer.date)
            val latestCommitDate = parseDateToTimestamp(latestTagCommit.commit.committer.date)

            if (remoteCommitDate < latestCommitDate) latestRelease else null
        }
        1 -> null
        2 -> if (!latestRelease.prerelease && BuildConfig.BUILD_TYPE != "release")
            latestRelease else null
        else -> latestRelease
    }
}

suspend fun checkReleaseUpdate(versionName: String = BuildConfig.VERSION_NAME): Release? {
    val localVersionBody = formatVersionName(versionName)
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    return try {
        val response: HttpResponse =
            client.get("https://api.github.com/repos/SkyDynamic/MaiproberPlus/releases/latest")
        val release: Release = response.body()
        val latestVersionBody = formatVersionName(release.tagName)

        when (compareVersion(localVersionBody, latestVersionBody)) {
            0 -> null
            1 -> null
            2 -> if (!release.prerelease && BuildConfig.BUILD_TYPE != "release") release else null
            else -> release
        }
    } catch (e: Exception) {
        Log.e("CheckUpdateUtils", "Failed to fetch latest release: ${e.message}")
        null
    } finally {
        client.close()
    }
}
