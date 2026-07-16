package com.nitokrisalpha.service

import qbittorrent.models.Torrent

fun interface DownloadedListener {

    fun process(torrent: Torrent)

}