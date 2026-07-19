package com.nitokrisalpha.service

import com.nitokrisalpha.entity.JavWork

interface Downloader {

    val basePath: String

    fun download(javWork: JavWork)

    fun download(magnets: List<String>)

    fun download(url: String)

    fun addDownloadedListener(listener: DownloadedListener)

    fun addRatioLimitListener(listener: RatioLimitListener)

    fun exists(hash: String): Boolean

    fun remove(magnet: String)
}