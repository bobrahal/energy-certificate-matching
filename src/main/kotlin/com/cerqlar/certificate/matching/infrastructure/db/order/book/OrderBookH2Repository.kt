package com.cerqlar.certificate.matching.infrastructure.db.order.book

import com.cerqlar.certificate.matching.domain.order.book.OrderBook
import com.cerqlar.certificate.matching.domain.order.book.OrderBookRepository
import org.springframework.stereotype.Component

@Component
class OrderBookH2Repository(private val crudRepository: OrderBookCRUDRepository) :
    OrderBookRepository {
    override fun save(orderBook: OrderBook): OrderBook {
        return crudRepository.save(OrderBookEntity.fromDomainEntity(orderBook)).toDomainEntity()
    }
}