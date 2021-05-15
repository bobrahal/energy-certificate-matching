package com.cerqlar.certificate.matching.infrastructure.db.agent.buyer

import com.cerqlar.certificate.matching.domain.agent.buyer.Buyer
import com.cerqlar.certificate.matching.domain.agent.buyer.BuyerId
import com.cerqlar.certificate.matching.domain.agent.buyer.BuyerRepository
import org.springframework.stereotype.Component

@Component
class BuyerH2Repository(private val crudRepository: BuyerCRUDRepository) : BuyerRepository {
    override fun fetchBuyerById(id: BuyerId): Buyer? {
        return crudRepository.findById(id.id).orElse(null)?.toDomainEntity()
    }

    override fun fetchBuyerByCode(code: String): Buyer? {
        return crudRepository.findByCode(code).orElse(null)?.toDomainEntity()
    }
}