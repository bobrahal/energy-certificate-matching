package com.cerqlar.certificate.matching.domain.order.book

import com.cerqlar.certificate.matching.contants.MatchType
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.interest.Interest
import java.util.*

class OrderBook(
    val id: OrderBookId = OrderBookId(0),
    var interest: Interest,
    var certificateBundle: CertificateBundle,
    var quantity: Int,
    var matchType: MatchType,
    var matchedDate: Date = Date()
)

data class OrderBookId(val id: Long)