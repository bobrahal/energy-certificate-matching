package com.cerqlar.certificate.matching.domain.agent.seller

interface SellerRepository {
    fun fetchSellerById(id: SellerId): Seller?
    fun fetchSellerByCode(code: String): Seller?
}