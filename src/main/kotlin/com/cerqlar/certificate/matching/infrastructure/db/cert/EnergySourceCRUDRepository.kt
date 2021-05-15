package com.cerqlar.certificate.matching.infrastructure.db.cert

import org.springframework.data.repository.CrudRepository
import java.util.*

interface EnergySourceCRUDRepository : CrudRepository<EnergySourceEntity, Long> {
    fun findByCode(code: String): Optional<EnergySourceEntity>
}