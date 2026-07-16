package com.nitokrisalpha.service

import com.nitokrisalpha.conf.PathConfig
import com.nitokrisalpha.entity.JavWork
import com.nitokrisalpha.entity.Status
import com.nitokrisalpha.repository.JavWorkRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import qbittorrent.models.Torrent
import java.io.File
import java.nio.file.Paths

@Service
class JavService {
    private val downloader: Downloader
    private val repository: JavWorkRepository
    private val pathConfig: PathConfig

    private val log = LoggerFactory.getLogger(javaClass)

    constructor(downloader: Downloader, pathConfig: PathConfig, repository: JavWorkRepository) {
        this.downloader = downloader
        this.pathConfig = pathConfig
        this.repository = repository
        if (pathConfig.from.isEmpty() || pathConfig.target.isEmpty()) {
            return
        }
        downloader.addDownloadedListener { torrent ->
            val hash = torrent.hash
            repository.findByHash(hash)?.let { work ->
                if (work.status == Status.DOWNLOADING) {
                    // 设置为已下载
                    work.status = Status.DOWNLOADED
                    repository.save(work)
                }
                if (work.status == Status.DOWNLOADED) {
                    // 执行移动
                    work.status = Status.MOVING
                    repository.save(work)
                    doMove(torrent)
                    work.status = Status.MOVED
                    repository.save(work)
                }
            }
        }

        downloader.addRatioLimitListener {
            println("Ratio limit listener:${it.ratio}")
        }
    }

    fun doMove(torrent: Torrent) {
        log.info("start move")
        val torrentSavePath = torrent.contentPath
        val downloaderPath = downloader.basePath
        val fromPath = torrentSavePath.replace(downloaderPath, pathConfig.from)
        val targetPath = torrentSavePath.replace(downloaderPath, pathConfig.target)
        println(fromPath)
        println(targetPath)
        val srcFile = File(fromPath)
        if(!srcFile.exists()) {
            log.warn("srcFile does not exist")
            return
        }
        if (srcFile.isFile) {
            log.info("start copy file")
            val name = srcFile.nameWithoutExtension
            val targetFile = Paths.get(pathConfig.target, name, srcFile.name).toFile()
            if (!targetFile.exists()) {
                targetFile.mkdirs()
                targetFile.createNewFile()
                srcFile.copyTo(targetFile, true)
            }
        } else {
            log.info("start copy directory")
            val targetFile = File(targetPath)
            if (!targetFile.exists()) {
                targetFile.mkdirs()
                targetFile.createNewFile()
                srcFile.copyRecursively(targetFile)
            }
        }
        log.info("copy end")
    }


    fun download(javWork: JavWork) {
        val findByHash = repository.findByHash(javWork.hash)
        if (findByHash != null) {
            println("该种子已入库")
            return
        }
        repository.save(javWork)
        if (javWork.hash != null && downloader.exists(javWork.hash)) {
            println("该种子已存在：${javWork.magnet}")
            return
        }
        javWork.status = Status.DOWNLOADING
        repository.save(javWork)
        downloader.download(javWork)
    }

}