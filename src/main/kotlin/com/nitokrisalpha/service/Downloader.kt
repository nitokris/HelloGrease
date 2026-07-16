package com.nitokrisalpha.service

import com.nitokrisalpha.entity.JavWork

interface Downloader {

    val basePath: String

    fun download(javWork: JavWork)

    fun addDownloadedListener(listener: DownloadedListener)

    fun addRatioLimitListener(listener: RatioLimitListener)

    fun exists(hash: String): Boolean
}