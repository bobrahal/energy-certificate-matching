package com.cerqlar.certificate.matching.domain.cert

interface EnergySourceRepository {
    fun fetchEnergySourceById(id: EnergySourceId): EnergySource?
    fun fetchEnergySourceByCode(code: String): EnergySource?
}