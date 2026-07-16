package com.nitokrisalpha.repository

import com.nitokrisalpha.entity.JavWork
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JavWorkRepository : JpaRepository<JavWork, Long> {

    fun findByMagnet(magnet: String): MutableList<JavWork>

    fun findByHash(hash: String): JavWork?

}