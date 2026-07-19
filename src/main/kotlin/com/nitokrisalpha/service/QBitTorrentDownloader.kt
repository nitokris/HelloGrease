package com.nitokrisalpha.service

import com.nitokrisalpha.entity.JavWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import qbittorrent.QBittorrentClient
import kotlin.concurrent.thread

@Component
class QBitTorrentDownloader(
    private val qBittorrentClient: QBittorrentClient
) : Downloader {

    private val log = LoggerFactory.getLogger(javaClass)

    private val downloadedListeners = arrayListOf<DownloadedListener>()
    private val ratioLimitListeners = arrayListOf<RatioLimitListener>()
    override val basePath: String
        get() = runBlocking {
            qBittorrentClient.getDefaultSavePath()
        }

    init {
        thread(isDaemon = true) {
            runBlocking {
                qBittorrentClient.observeMainData()
                    .retry(Long.MAX_VALUE) { cause ->
                        log.error("监听异常，5秒后重连...", cause)
                        delay(5_000)
                        true
                    }
                    .collect { mainData ->
                        log.info("正常读取到qbitorrent数据")
                        mainData.torrents.forEach { (_, torrent) ->
                            if (torrent.progress == 1f) {
                                downloadedListeners.forEach { it.process(torrent) }
                            }
                            if (torrent.ratio == 2f) {
                                ratioLimitListeners.forEach { it.process(torrent) }
                            }
                        }
                    }
            }
        }
    }

    override fun download(javWork: JavWork) {
        runBlocking(Dispatchers.IO) {

            javWork.magnet?.let { magnet ->
                qBittorrentClient.addTorrent {
                    urls += magnet
                }
            }

        }
    }

    override fun download(magnets: List<String>) {
        runBlocking(Dispatchers.IO) {
            qBittorrentClient.addTorrent {
                urls += magnets
            }
        }
    }

    override fun download(url: String) {
        runBlocking(Dispatchers.IO) {
            qBittorrentClient.addTorrent {
                urls += url
            }
        }
    }

    override fun exists(hash: String): Boolean {
        return runBlocking(Dispatchers.IO) {
            qBittorrentClient.getTorrents(hashes = listOf(hash)).isNotEmpty()
        }
    }

    override fun remove(magnet: String) {
        runBlocking(Dispatchers.IO) {
            val torrent = qBittorrentClient.getTorrents().find { it.magnetUri == magnet }
            if (torrent != null) {
                qBittorrentClient.deleteTorrents(hashes = listOf(torrent.hash), deleteFiles = true)
            }
        }
    }

    override fun addDownloadedListener(listener: DownloadedListener) {
        downloadedListeners += listener
    }

    override fun addRatioLimitListener(listener: RatioLimitListener) {
        ratioLimitListeners += listener
    }


}