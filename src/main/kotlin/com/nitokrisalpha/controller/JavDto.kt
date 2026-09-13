package com.nitokrisalpha.controller

import com.nitokrisalpha.entity.JavWork

data class JavDto(
    val name: String,
    val magnet: String,
    val code: String
) {
    fun toEntity(): JavWork {
        return JavWork(name, magnet, code)
    }
}