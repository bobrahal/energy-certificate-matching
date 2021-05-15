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

        // get the list of pending certificate bundles available for this energy source ordered by quantity descending
        // the list is order by descending quantity, giving priority to the certificate bundles with the largest quantity for a specific energy source
        val certificateBundles: List<CertificateBundle> =
            certificateBundleRepository.findAllByEnergySourceAndStatus(energySource, Status.PENDING)

        var totalSellQuantity = 0
        val orderBooks: MutableList<OrderBook> = mutableListOf()
        var matchFound = false

        /**
         * loop through the fetched certificate bundles
         * break this loop only when the total quantity of the certificate bundles for the same energy source is greater or equal to the quantity in the interest
         * (meaning that when the interest can be fully matched)
         */
        for (certificateBundle in certificateBundles) {

            totalSellQuantity += certificateBundle.quantity

            if (totalSellQuantity >= interest.quantity) {

                matchFound = true
                orderBooks.add(
                    OrderBook(
                        OrderBookId(0L), // id will be auto-generated anyway in db
                        interest,
                        certificateBundle,
                        certificateBundle.quantity - (totalSellQuantity - interest.quantity), // remaining amount to fully close the interest
                        when (totalSellQuantity) {
                            interest.quantity -> MatchType.FULL
                            else -> MatchType.PARTIAL
                        }
                    )
                )
                break
            } else {
                // if total quantity is still less than interest's quantity, add it in the orderBook list and move on to the next iteration
                orderBooks.add(
                    OrderBook(
                        OrderBookId(0L),
                        interest,
                        certificateBundle,
                        certificateBundle.quantity, // order book entry quantity is also the certificate bundle quantity since it's still less than the interest quantity
                        MatchType.FULL
                    )
                )
            }
        }

        // iterated over the full list and no match was found
        // interest not matched. Throw OrderNotMatchedException exception
        if (!matchFound) {
            throw OrderNotMatchedException(
                message = "no combination of certificate bundles found that match interest"
            )
        }

        // Match found,
        handleMatch(interest, orderBooks)

        // return successful event
        return OrderMatchedEvent(orderBooks)
    }

    /**
     * Close interest
     * for each entry in the order book
     *  - log the order book entry in db
     *  - Close certificate bundle if matchType is FULL,
     *  otherwise, decrement the quantity by the remaining quantity
     */
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