package com.nitokrisalpha.service

import com.nitokrisalpha.entity.JavWork
import kotlinx.coroutines.Dispatchers
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
        thread {
            runBlocking(Dispatchers.IO) {
                while (true) {
                    try {
                        qBittorrentClient.observeMainData().collect { mainData ->
                            mainData.torrents.forEach { torrentInfo ->
                                val torrent = torrentInfo.value
                                if (torrent.progress == 1f) {
                                    downloadedListeners.forEach {
                                        it.process(torrent)
                                    }
                                }
                                if (torrent.ratio == 2f) {
                                    ratioLimitListeners.forEach {
                                        it.process(torrent)
                                    }
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        log.error("Watching torrent error", e)
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

    override fun addDownloadedListener(listener: DownloadedListener) {
        downloadedListeners += listener
    }

    override fun addRatioLimitListener(listener: RatioLimitListener) {
        ratioLimitListeners += listener
    }


}