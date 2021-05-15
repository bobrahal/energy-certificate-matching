package com.cerqlar.certificate.matching.infrastructure.http.presentation

import com.cerqlar.certificate.matching.exceptions.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class FailureJSONPresenter {
    fun presentSellerNotFound(e: SellerNotFoundException) =
        FailureJSONResponse(
            message = "Unable to create sell order. Seller ${e.seller.code} not found",
            code = "ERR-1"
        )

    fun presentBuyerNotFound(e: BuyerNotFoundException) =
        FailureJSONResponse(
            message = "Unable to create buy order. Buyer ${e.buyer.code} not found",
            code = "ERR-2"
        )

    fun presentEnergySourceNotFound(e: EnergySourceNotFoundException) =
        FailureJSONResponse(
            message = "Unable to create order. Energy source ${e.energySource.code} not found",
            code = "ERR-3"
        )

    fun presentInvalidQuantity(e: InvalidQuantityException) =
        FailureJSONResponse(
            message = "Unable to create order. Quantity should be greater than zero",
            code = "ERR-4"
        )

    fun presentOrderNotMatched(e: OrderNotMatchedException) =
        FailureJSONResponse(
            message = "Order not matched: ${e.message}",
            code = "ERR-4"
        )
}

data class FailureJSONResponse(val message: String, val code: String)
