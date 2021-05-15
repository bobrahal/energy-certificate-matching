package com.cerqlar.certificate.matching.infrastructure.db.agent.seller

import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.agent.seller.SellerId
import com.cerqlar.certificate.matching.domain.agent.seller.SellerRepository
import org.springframework.stereotype.Component

@Component
class SellerH2Repository(private val crudRepository: SellerCRUDRepository) : SellerRepository {
    override fun fetchSellerById(id: SellerId): Seller? {
        return crudRepository.findById(id.id).orElse(null)?.toDomainEntity()
    }

    override fun fetchSellerByCode(code: String): Seller? {
        return crudRepository.findByCode(code).orElse(null)?.toDomainEntity()
    }
}