package com.cerqlar.certificate.matching.service

import com.cerqlar.certificate.matching.domain.agent.buyer.BuyerRepository
import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.domain.order.interest.InterestRepository
import com.cerqlar.certificate.matching.exceptions.BuyerNotFoundException
import com.cerqlar.certificate.matching.exceptions.InvalidQuantityException
import org.springframework.stereotype.Service

@Service
class BuyOrderService(
    val buyerRepository: BuyerRepository,
    val interestRepository: InterestRepository,
    val orderBookService: OrderBookService
) {
    fun create(interest: Interest): BuyOrderCreatedEvent {

        println("trying to find buyer ${interest.buyer.code} in db")

        // check if buyer creating the sell order actually exists in db
        val buyer = buyerRepository.fetchBuyerByCode(interest.buyer.code)
            ?: throw BuyerNotFoundException(
                buyer = interest.buyer
            )

        when (interest.quantity <= 0) {
            true -> throw InvalidQuantityException()
        }

        interest.buyer = buyer
        buyer.addInterest(interest)

        // create interest and execute match
        orderBookService.executeMatchInterest(interestRepository.save(interest))

        return BuyOrderCreatedEvent(
            interest = interest
        )
    }
}

data class BuyOrderCreatedEvent(val interest: Interest)
