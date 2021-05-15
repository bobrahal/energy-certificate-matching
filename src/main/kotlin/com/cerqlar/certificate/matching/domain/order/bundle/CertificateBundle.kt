package com.cerqlar.certificate.matching.domain.order.bundle

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.cert.EnergySource
import java.util.*

class CertificateBundle(
    val id: CertificateBundleId = CertificateBundleId(0),
    var energySource: EnergySource,
    var quantity: Int,
    var issuer: String,
    var issueDate: Date,
    var seller: Seller,
    var status: Status = Status.PENDING
)


data class CertificateBundleId(val id: Long)