package com.cerqlar.certificate.matching.infrastructure.db.order.bundle

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceEntity
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import javax.persistence.OrderBy


interface CertificateBundleCRUDRepository : CrudRepository<CertificateBundleEntity, Long> {

    fun findAllByEnergySourceAndStatus(energySource: EnergySourceEntity, status: Status, by: Sort): List<CertificateBundleEntity>

    @Modifying
    @Query(value = "UPDATE CERTIFICATE_BUNDLES set QUANTITY = QUANTITY - ?1 where ID = ?2", nativeQuery = true)
    fun updateQuantity(quantity: Int, id: Long)

    @Modifying
    @Query(value = "UPDATE CERTIFICATE_BUNDLES set STATUS = ?1 where ID = ?2", nativeQuery = true)
    fun updateStatus(status: String, id: Long)
}