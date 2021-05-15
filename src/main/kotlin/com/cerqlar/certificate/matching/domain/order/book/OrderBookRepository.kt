package com.cerqlar.certificate.matching.domain.order.book

interface OrderBookRepository {
    fun save(orderBook: OrderBook) : OrderBook
}