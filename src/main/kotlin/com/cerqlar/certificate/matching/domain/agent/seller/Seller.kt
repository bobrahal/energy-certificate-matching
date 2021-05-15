package com.cerqlar.certificate.matching.domain.agent.seller

import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle

class Seller(
    var id: SellerId = SellerId(0),
    var code: String,
    var name: String = "",
    var certificateBundles: MutableList<CertificateBundle> = mutableListOf()
) {
    fun addCertificate(certificateBundle: CertificateBundle) {
        certificateBundles.add(certificateBundle)
    }
}

data class SellerId(val id: Long)