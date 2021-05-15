package com.cerqlar.certificate.matching

import com.cerqlar.certificate.matching.infrastructure.db.agent.buyer.BuyerCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.agent.buyer.BuyerEntity
import com.cerqlar.certificate.matching.infrastructure.db.agent.seller.SellerCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.agent.seller.SellerEntity
import com.cerqlar.certificate.matching.infrastructure.db.agent.seller.SellerH2Repository
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceEntity
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceH2Repository
import com.cerqlar.certificate.matching.infrastructure.db.order.bundle.CertificateBundleH2Repository
import com.cerqlar.certificate.matching.service.SellOrderService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(basePackages = ["com.cerqlar.certificate.matching.infrastructure.db"])
@EntityScan("com.cerqlar.certificate.matching.infrastructure.db")
@SpringBootApplication(scanBasePackages = ["com.cerqlar.certificate.matching"])
class Application {
    private val log = LoggerFactory.getLogger(Application::class.java)

    /**
     * Creating some data for Sellers, Buyers and Energy source
     * This is only for the sake of simplicity and should not be adopted for production apps
     */
    @Bean
    fun init(
        sellerRepository: SellerCRUDRepository,
        energySourceRepository: EnergySourceCRUDRepository,
        buyerRepository: BuyerCRUDRepository
    ) = CommandLineRunner {
        // save a couple of energy sources
        energySourceRepository.save(EnergySourceEntity(id = 1L, code = "SLR", name = "Solar"))
        energySourceRepository.save(EnergySourceEntity(id = 2L, code = "WND", name = "Wind"))
        energySourceRepository.save(EnergySourceEntity(id = 3L, code = "WTR", name = "Water"))

        // save a couple of sellers
        sellerRepository.save(SellerEntity(id = 1L, code = "00001", name = "Seller 1"))
        sellerRepository.save(SellerEntity(id = 2L, code = "00002", name = "Seller 2"))
        sellerRepository.save(SellerEntity(id = 3L, code = "00003", name = "Seller 3"))
        sellerRepository.save(SellerEntity(id = 4L, code = "00004", name = "Seller 4"))
        sellerRepository.save(SellerEntity(id = 5L, code = "00005", name = "Seller 5"))

        // save a couple of buyers
        buyerRepository.save(BuyerEntity(id = 1L, code = "00001", name = "Buyer 1"))
        buyerRepository.save(BuyerEntity(id = 2L, code = "00002", name = "Buyer 2"))
        buyerRepository.save(BuyerEntity(id = 3L, code = "00003", name = "Buyer 3"))
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
