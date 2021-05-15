package com.cerqlar.certificate.matching.service

import com.cerqlar.certificate.matching.contants.MatchType
import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.cert.EnergySourceRepository
import com.cerqlar.certificate.matching.domain.order.book.OrderBook
import com.cerqlar.certificate.matching.domain.order.book.OrderBookId
import com.cerqlar.certificate.matching.domain.order.book.OrderBookRepository
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleRepository
import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.domain.order.interest.InterestRepository
import com.cerqlar.certificate.matching.exceptions.OrderNotMatchedException
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderBookService(
    val interestRepository: InterestRepository,
    val certificateBundleRepository: CertificateBundleRepository,
    val orderBookRepository: OrderBookRepository,
    val energySourceRepository: EnergySourceRepository
) : IOrderBookService {

    override fun executeMatchInterest(interest: Interest): OrderMatchedEvent? {

        // check if energySource actually exists in db
        val energySource =
            energySourceRepository.fetchEnergySourceByCode(interest.certificateCode) ?: throw OrderNotMatchedException(
                message = "certificate energy source ${interest.certificateCode} not found"
            )

        // get the list of certificate bundles available for this energy source ordered by quantity descending
        val certificateBundles: List<CertificateBundle> =
            certificateBundleRepository.findAllByEnergySourceAndStatus(energySource, Status.PENDING)

        var totalSellQuantity: Int = 0
        var orderBooks: MutableList<OrderBook> = mutableListOf()
        var matchFound: Boolean = false

        for (certificateBundle in certificateBundles) {

            totalSellQuantity += certificateBundle.quantity

            if (totalSellQuantity >= interest.quantity) {

                matchFound = true
                orderBooks.add(
                    OrderBook(
                        OrderBookId(0L),
                        interest,
                        certificateBundle,
                        certificateBundle.quantity - (totalSellQuantity - interest.quantity), // remaining amount
                        when (totalSellQuantity) {
                            interest.quantity -> MatchType.FULL
                            else -> MatchType.PARTIAL
                        }
                    )
                )
                break
            } else {

                orderBooks.add(
                    OrderBook(
                        OrderBookId(0L),
                        interest,
                        certificateBundle,
                        certificateBundle.quantity,
                        MatchType.FULL
                    )
                )
            }
        }

        if (!matchFound) {
            throw OrderNotMatchedException(
                message = "no combination of certificate bundles found that match interest"
            )
        }

        handleMatch(interest, orderBooks)

        return OrderMatchedEvent(orderBooks)
    }

    fun handleMatch(interest: Interest, orderBooks: MutableList<OrderBook>) {

        // close interest
        interestRepository.update(Status.CLOSED.toString(), interest.id.id)

        for (orderBook in orderBooks) {
            orderBookRepository.save(orderBook)
            certificateBundleRepository.updateQuantity(orderBook.quantity, orderBook.certificateBundle.id.id)

            if (orderBook.matchType == MatchType.FULL) {
                certificateBundleRepository.update(Status.CLOSED.toString(), orderBook.certificateBundle.id.id)
            }
        }
    }
}

data class OrderMatchedEvent(val orderBooks: List<OrderBook>)