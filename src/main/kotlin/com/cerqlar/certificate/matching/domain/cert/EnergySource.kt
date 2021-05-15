package com.cerqlar.certificate.matching.domain.cert

data class EnergySource(
    val id: EnergySourceId = EnergySourceId(0),
    val code: String,
    val name: String = ""
)

data class EnergySourceId(val id: Long)