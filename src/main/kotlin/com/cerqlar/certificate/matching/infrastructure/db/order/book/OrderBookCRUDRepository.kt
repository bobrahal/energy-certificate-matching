package com.cerqlar.certificate.matching.infrastructure.db.order.book

import org.springframework.data.repository.CrudRepository


interface OrderBookCRUDRepository : CrudRepository<OrderBookEntity, Long>