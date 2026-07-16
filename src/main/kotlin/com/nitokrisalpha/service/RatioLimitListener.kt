package com.nitokrisalpha.service

import qbittorrent.models.Torrent

fun interface RatioLimitListener {

    fun process(torrent: Torrent)
}