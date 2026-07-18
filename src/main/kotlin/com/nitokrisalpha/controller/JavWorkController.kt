package com.nitokrisalpha.controller

import com.nitokrisalpha.service.JavService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/jav")
@RestController
class JavWorkController(
    val javService: JavService
) {


    @PostMapping("")
    fun download(
        @RequestBody dto: JavDto
    ) {
        javService.download(dto.toEntity())
    }

    @PostMapping("/batch")
    fun batchDownload(@RequestBody dtos: List<String>) {
        javService.batchDownload(dtos)
    }

}