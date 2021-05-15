package com.cerqlar.certificate.matching.exceptions

import com.cerqlar.certificate.matching.domain.agent.buyer.Buyer
import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.cert.EnergySource

class SellerNotFoundException(val seller: Seller) : RuntimeException()
class EnergySourceNotFoundException(val energySource: EnergySource) : RuntimeException()
class InvalidQuantityException : RuntimeException()

class BuyerNotFoundException(val buyer: Buyer) : RuntimeException()

class OrderNotMatchedException(override val message: String) : RuntimeException()