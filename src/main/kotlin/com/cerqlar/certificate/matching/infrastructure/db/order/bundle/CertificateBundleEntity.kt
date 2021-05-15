package com.cerqlar.certificate.matching.infrastructure.db.order.bundle

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleId
import com.cerqlar.certificate.matching.infrastructure.db.agent.seller.SellerEntity
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "certificate_bundles")
data class CertificateBundleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long,
    @ManyToOne
    @JoinColumn(name = "energy_source_id")
    var energySource: EnergySourceEntity,
    var quantity: Int,
    var issuer: String,
    var issueDate: Date,
    @ManyToOne
    @JoinColumn(name = "seller_id")
    var seller: SellerEntity,
    @Enumerated(EnumType.STRING)
    val status: Status
) {
    companion object {
        fun fromDomainEntity(certificateBundle: CertificateBundle): CertificateBundleEntity = CertificateBundleEntity(
            id = certificateBundle.id.id,
            energySource = EnergySourceEntity.fromDomainEntity(certificateBundle.energySource),
            quantity = certificateBundle.quantity,
            issuer = certificateBundle.issuer,
            issueDate = certificateBundle.issueDate,
            seller = SellerEntity.fromDomainEntity(certificateBundle.seller),
            status = certificateBundle.status
        )
    }

    fun toDomainEntity(): CertificateBundle = CertificateBundle(
        id = CertificateBundleId(id),
        energySource = energySource.toDomainEntity(),
        quantity = quantity,
        issuer = issuer,
        issueDate = issueDate,
        seller = seller.toDomainEntity(),
        status = status
    )
}
