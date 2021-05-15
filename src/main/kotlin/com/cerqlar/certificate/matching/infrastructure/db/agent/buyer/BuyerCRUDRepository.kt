package com.cerqlar.certificate.matching.infrastructure.db.agent.buyer

import org.springframework.data.repository.CrudRepository
import java.util.*

interface BuyerCRUDRepository : CrudRepository<BuyerEntity, Long>{
    fun findByCode(code: String): Optional<BuyerEntity>
}