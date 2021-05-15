package com.cerqlar.certificate.matching.service

import com.cerqlar.certificate.matching.domain.order.interest.Interest

interface IOrderBookService {
    fun executeMatchInterest(interest: Interest): OrderMatchedEvent?
}