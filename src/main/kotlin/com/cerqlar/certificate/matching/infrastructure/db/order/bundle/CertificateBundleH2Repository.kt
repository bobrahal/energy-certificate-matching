package com.cerqlar.certificate.matching.infrastructure.db.order.bundle

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleId
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleRepository
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceEntity
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CertificateBundleH2Repository(private val crudRepository: CertificateBundleCRUDRepository) :
    CertificateBundleRepository {

    override fun fetchCertificateBundleById(id: CertificateBundleId): CertificateBundle? {
        return crudRepository.findById(id.id).orElse(null)?.toDomainEntity()
    }

    override fun save(certificateBundle: CertificateBundle): CertificateBundle {
        return crudRepository.save(CertificateBundleEntity.fromDomainEntity(certificateBundle)).toDomainEntity()
    }

    override fun findAllByEnergySourceAndStatus(energySource: EnergySource, status: Status): List<CertificateBundle> {

        val certificateBundles: List<CertificateBundle> =
            crudRepository.findAllByEnergySourceAndStatus(
                EnergySourceEntity.fromDomainEntity(energySource),
                status,
                Sort.by(Sort.Direction.DESC, "quantity")
            ).map { it.toDomainEntity() }

        certificateBundles.forEach { c ->
            println("certificate bundle id #${c.id} quantity ${c.quantity}")
        }

        return certificateBundles
    }

    @Transactional
    override fun updateQuantity(quantity: Int, id: Long) = crudRepository.updateQuantity(quantity, id)

    @Transactional
    override fun update(status: String, id: Long) = crudRepository.updateStatus(status, id)
}