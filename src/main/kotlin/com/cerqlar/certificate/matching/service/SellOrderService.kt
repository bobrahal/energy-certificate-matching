package com.cerqlar.certificate.matching.service

import com.cerqlar.certificate.matching.domain.agent.seller.SellerRepository
import com.cerqlar.certificate.matching.domain.cert.EnergySourceRepository
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleRepository
import com.cerqlar.certificate.matching.exceptions.EnergySourceNotFoundException
import com.cerqlar.certificate.matching.exceptions.InvalidQuantityException
import com.cerqlar.certificate.matching.exceptions.SellerNotFoundException
import org.springframework.stereotype.Service

@Service
class SellOrderService(
    val sellerRepository: SellerRepository,
    val energySourceRepository: EnergySourceRepository,
    val certificateBundleRepository: CertificateBundleRepository
) {
    fun create(certificateBundle: CertificateBundle): SellOrderCreatedEvent {

        println("trying to find seller ${certificateBundle.seller.code} in db")

        // check if seller creating the sell order actually exists in db
        val seller = sellerRepository.fetchSellerByCode(certificateBundle.seller.code)
            ?: throw SellerNotFoundException(
                seller = certificateBundle.seller
            )

        // check if energySource actually exists in db
        val energySource = energySourceRepository.fetchEnergySourceByCode(certificateBundle.energySource.code)
            ?: throw EnergySourceNotFoundException(
                energySource = certificateBundle.energySource
            )

        when (certificateBundle.quantity <= 0) {
            true -> throw InvalidQuantityException()
        }

        certificateBundle.seller = seller
        seller.addCertificate(certificateBundle)
        certificateBundle.energySource = energySource

        certificateBundleRepository.save(certificateBundle)

        return SellOrderCreatedEvent(
            certificateBundle = certificateBundle
        )
    }
}

data class SellOrderCreatedEvent(val certificateBundle: CertificateBundle)
