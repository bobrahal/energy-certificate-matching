package com.cerqlar.certificate.matching.infrastructure.db.cert

import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.domain.cert.EnergySourceId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ENERGY_SOURCES")
data class EnergySourceEntity(
    @Id
    val id: Long,
    val code: String,
    val name: String
){
    companion object {
        fun fromDomainEntity(energySource: EnergySource): EnergySourceEntity = EnergySourceEntity (
            id = energySource.id.id,
            code = energySource.code,
            name = energySource.name
        )
    }

    fun toDomainEntity(): EnergySource = EnergySource (
        id = EnergySourceId(id),
        code = code,
        name = name
    )
}