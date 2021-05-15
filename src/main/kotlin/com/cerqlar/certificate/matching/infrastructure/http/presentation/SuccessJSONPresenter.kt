package com.cerqlar.certificate.matching.infrastructure.http.presentation

import com.cerqlar.certificate.matching.service.BuyOrderCreatedEvent
import com.cerqlar.certificate.matching.service.SellOrderCreatedEvent
import org.springframework.stereotype.Component

@Component
class SuccessJSONPresenter {

    fun present(event: SellOrderCreatedEvent) = SellOrderCreatedJSONResponse(
        sellerCode = event.certificateBundle.seller.code,
        energySource = event.certificateBundle.energySource.name,
        quantity = event.certificateBundle.quantity
    )

    fun present(event: BuyOrderCreatedEvent) = BuyOrderCreatedJSONResponse(
        buyerCode = event.interest.buyer.code,
        certificateCode = event.interest.certificateCode,
        quantity = event.interest.quantity
    )
}

class SellOrderCreatedJSONResponse(
    private val sellerCode: String,
    private val energySource: String,
    private val quantity: Int
) {
    val message = "Sell Order (Certificate Bundle) for $quantity $energySource certificates successfully created for seller $sellerCode"
    val code = "Success"
}

class BuyOrderCreatedJSONResponse(
    private val buyerCode: String,
    private val certificateCode: String,
    private val quantity: Int
) {
    val message = "Buy Order (Interest) for $quantity $certificateCode certificates successfully created for buyer $buyerCode"
    val code = "Success"
}