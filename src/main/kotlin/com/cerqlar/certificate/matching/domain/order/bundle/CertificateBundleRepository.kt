package com.cerqlar.certificate.matching.domain.order.bundle

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.cert.EnergySource

interface CertificateBundleRepository {
    fun fetchCertificateBundleById(id: CertificateBundleId) : CertificateBundle?
    fun save(certificateBundle: CertificateBundle) : CertificateBundle

    fun findAllByEnergySourceAndStatus(energySource: EnergySource, status: Status): List<CertificateBundle>
    fun updateQuantity(quantity: Int, id: Long)

    fun update(status: String, id: Long)
}