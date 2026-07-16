package com.nitokrisalpha

import com.nitokrisalpha.conf.PathConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
class JavdbApplication

fun main(args: Array<String>) {
    runApplication<JavdbApplication>(*args)
}
