package com.cerqlar.certificate.matching.infrastructure.db.order.interest

import com.cerqlar.certificate.matching.contants.Status
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface InterestCRUDRepository : CrudRepository<InterestEntity, Long>{

    @Modifying
    @Query(value = "UPDATE INTERESTS set STATUS = ?1 where ID = ?2", nativeQuery = true)
    fun updateStatus(status: String, id: Long)
}