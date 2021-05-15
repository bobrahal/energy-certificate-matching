package com.cerqlar.certificate.matching.infrastructure.http.routing

import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.infrastructure.http.presentation.SuccessJSONPresenter
import com.cerqlar.certificate.matching.service.SellOrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SellOrderController(
    private val sellOrderService: SellOrderService,
    private val successJSONPresenter: SuccessJSONPresenter
) {

    @PostMapping("/add-bundle")
    fun createSellOrder(
        @RequestBody certificateBundle: CertificateBundle,
    ) = successJSONPresenter.present(
        sellOrderService.create(
            certificateBundle
        )
    )
}