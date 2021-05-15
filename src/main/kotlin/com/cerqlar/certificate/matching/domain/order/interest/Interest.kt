package com.cerqlar.certificate.matching.domain.order.interest

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.agent.buyer.Buyer
import java.util.*

class Interest(
    val id: InterestId = InterestId(0),
    var certificateCode: String,
    var quantity: Int,
    var buyer: Buyer,
    var status: Status = Status.PENDING,
    var createdDate: Date = Date()
)


data class InterestId(val id: Long)