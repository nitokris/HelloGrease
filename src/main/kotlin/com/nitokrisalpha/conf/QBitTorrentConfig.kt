package com.nitokrisalpha.conf

import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import qbittorrent.QBittorrentClient
import kotlin.time.Duration.Companion.seconds

@Component
class QBitTorrentConfig {

    @Bean
    fun qbittorrent(
        @Value("\${qbittorrent.base-url}") baseUrl: String,
        @Value("\${qbittorrent.username}") username: String,
        @Value("\${qbittorrent.password}") password: String,
    ) = QBittorrentClient(
        baseUrl = baseUrl,
        username = username,
        password = password,
        syncInterval = 5.seconds,
        httpClient = HttpClient(),
        dispatcher = Dispatchers.Default,
    )
}
