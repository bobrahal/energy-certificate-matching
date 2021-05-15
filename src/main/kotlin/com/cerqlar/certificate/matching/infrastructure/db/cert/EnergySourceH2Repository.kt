package com.cerqlar.certificate.matching.infrastructure.db.cert

import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.domain.cert.EnergySourceId
import com.cerqlar.certificate.matching.domain.cert.EnergySourceRepository
import org.springframework.stereotype.Component

@Component
class EnergySourceH2Repository(private val crudRepository: EnergySourceCRUDRepository) : EnergySourceRepository {
    override fun fetchEnergySourceById(id: EnergySourceId): EnergySource? {
        return crudRepository.findById(id.id).orElse(null)?.toDomainEntity()
    }

    override fun fetchEnergySourceByCode(code: String): EnergySource? {
        return crudRepository.findByCode(code).orElse(null)?.toDomainEntity()
    }
}