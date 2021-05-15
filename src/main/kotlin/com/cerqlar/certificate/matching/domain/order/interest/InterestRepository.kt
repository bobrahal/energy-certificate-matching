package com.cerqlar.certificate.matching.domain.order.interest

import com.cerqlar.certificate.matching.contants.Status

interface InterestRepository {
    fun fetchInterestById(id: InterestId) : Interest?
    fun save(certificateBundle: Interest) : Interest
    fun update(status: String, id: Long)
}