package com.cerqlar.certificate.matching.domain.agent.buyer

import com.cerqlar.certificate.matching.domain.order.interest.Interest

class Buyer(
    var id: BuyerId = BuyerId(0),
    var code: String,
    var name: String = "",
    var interests: MutableList<Interest> = mutableListOf()
) {
    fun addInterest(interest: Interest) {
        interests.add(interest)
    }
}

data class BuyerId(val id: Long)