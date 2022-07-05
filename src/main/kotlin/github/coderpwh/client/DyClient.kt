package github.coderpwh.client

import com.alibaba.fastjson.JSON
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.io.*


/**
 * @author coderpwh
 * @date 2022/7/4 20:01
 */
class DyClient {
    private val client by lazy {
        HttpClient(CIO) {
            install(UserAgent) {
                agent =
                    "Mozilla/5.0 (Linux; Android 11; SAMSUNG SM-G973U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/14.2 Chrome/87.0.4280.141 Mobile Safari/537.36"
            }
        }
    }

    suspend fun getVideoId(url: String): String {
        var resp = client.get(url)
        var originUrl = resp.request.url.toString()
        var videoRg = "video/(\\d+)?".toRegex()
        var result = videoRg.find(originUrl)?.groups?.get(1)?.value
        return result ?: ""
    }

    suspend fun getVideoDlUrl(vid: String): String {
        var vUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=${vid}"
        var resp = client.get(vUrl)
        var resultJsonText: String = resp.body()
        var jsonObject = JSON.parseObject(resultJsonText)
        var resultUrl = jsonObject.getJSONArray("item_list").getJSONObject(0)
            .getJSONObject("video")
            .getJSONObject("play_addr")
            .getJSONArray("url_list")
            .getString(0)
        return resultUrl.replace("playwm", "play")
    }

    suspend fun download(url: String, fileName: String) {
        var channel = client.get(url).bodyAsChannel()
        var file = File("${fileName}.mp4")
        while (!channel.isClosedForRead) {
            var packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
            while (!packet.isEmpty) {
                var bytes = packet.readBytes()
                file.appendBytes(bytes)
            }
        }
    }

    suspend fun getByteArray(url: String): ByteArray {
        return client.get(url).bodyAsChannel().toByteArray()
    }

    fun exec(url: String): String = runBlocking {
        var vid = getVideoId(url)
        if (vid.isEmpty()) {
            return@runBlocking ""
        }
        var vUrl = getVideoDlUrl(vid)
        if (vUrl.isEmpty()) {
            return@runBlocking ""
        }
        return@runBlocking vUrl
    }
}
