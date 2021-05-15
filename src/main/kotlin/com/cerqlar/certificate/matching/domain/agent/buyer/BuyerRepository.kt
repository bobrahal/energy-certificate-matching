package com.cerqlar.certificate.matching.domain.agent.buyer

interface BuyerRepository {
    fun fetchBuyerById(id: BuyerId): Buyer?
    fun fetchBuyerByCode(code: String): Buyer?
}