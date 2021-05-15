package com.cerqlar.certificate.matching.infrastructure.http.routing

import com.cerqlar.certificate.matching.exceptions.*
import com.cerqlar.certificate.matching.infrastructure.http.presentation.FailureJSONPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice(assignableTypes = [SellOrderController::class, BuyOrderController::class])
class ControllerAdvice(private val presenter: FailureJSONPresenter) {

    @ExceptionHandler(SellerNotFoundException::class)
    fun sellerNotFound(e: SellerNotFoundException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            presenter.presentSellerNotFound(e)
        )

    @ExceptionHandler(EnergySourceNotFoundException::class)
    fun energySourceNotFound(e: EnergySourceNotFoundException) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        presenter.presentEnergySourceNotFound(e)
    )

    @ExceptionHandler(InvalidQuantityException::class)
    fun invalidQuantity(e: InvalidQuantityException) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        presenter.presentInvalidQuantity(e)
    )

    @ExceptionHandler(BuyerNotFoundException::class)
    fun buyerNotFound(e: BuyerNotFoundException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            presenter.presentBuyerNotFound(e)
        )

    @ExceptionHandler(OrderNotMatchedException::class)
    fun orderNotMatched(e: OrderNotMatchedException) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        presenter.presentOrderNotMatched(e)
    )
}