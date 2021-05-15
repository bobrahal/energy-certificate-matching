package com.cerqlar.certificate.matching.infrastructure.db.agent.seller

import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.agent.seller.SellerId
import javax.persistence.*

@Entity
@Table(name = "SELLER")
data class SellerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long,
    var code: String,
    var name: String
) {
    companion object {
        fun fromDomainEntity(seller: Seller): SellerEntity = SellerEntity(
            id = seller.id.id,
            code = seller.code,
            name = seller.name
        )
    }

    fun toDomainEntity(): Seller = Seller(
        id = SellerId(id),
        code = code,
        name = name
    )
}