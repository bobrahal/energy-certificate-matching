package com.cerqlar.certificate.matching.infrastructure.db.agent.seller

import org.springframework.data.repository.CrudRepository
import java.util.*

interface SellerCRUDRepository : CrudRepository<SellerEntity, Long>{
    fun findByCode(code: String): Optional<SellerEntity>
}