package com.cerqlar.certificate.matching.infrastructure.db.agent.buyer

import com.cerqlar.certificate.matching.domain.agent.buyer.Buyer
import com.cerqlar.certificate.matching.domain.agent.buyer.BuyerId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "BUYER")
data class BuyerEntity(
    @Id
    var id: Long,
    var code: String,
    var name: String
) {
    companion object {
        fun fromDomainEntity(buyer: Buyer): BuyerEntity = BuyerEntity(
            id = buyer.id.id,
            code = buyer.code,
            name = buyer.name
        )
    }

    fun toDomainEntity(): Buyer = Buyer(
        id = BuyerId(id),
        code = code,
        name = name
    )
}