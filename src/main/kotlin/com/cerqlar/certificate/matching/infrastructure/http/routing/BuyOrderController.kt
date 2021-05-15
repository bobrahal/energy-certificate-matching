package com.cerqlar.certificate.matching.infrastructure.http.routing

import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.infrastructure.http.presentation.SuccessJSONPresenter
import com.cerqlar.certificate.matching.service.BuyOrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BuyOrderController(
    private val buyOrderService: BuyOrderService,
    private val successJSONPresenter: SuccessJSONPresenter
) {

    @PostMapping("/add-interest")
    fun createBuyOrder(
        @RequestBody interest: Interest
    ) = successJSONPresenter.present(
        buyOrderService.create(interest)
    )
}