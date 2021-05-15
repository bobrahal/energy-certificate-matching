package com.cerqlar.certificate.matching.infrastructure.db.order.interest

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.domain.order.interest.InterestId
import com.cerqlar.certificate.matching.infrastructure.db.agent.buyer.BuyerEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "interests")
data class InterestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long,

    var quantity: Int,
    var certificateCode: String,

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    var buyer: BuyerEntity,

    @Enumerated(EnumType.STRING)
    val status: Status,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    val createdDate: Date
) {
    companion object {
        fun fromDomainEntity(interest: Interest): InterestEntity = InterestEntity(
            id = interest.id.id,
            quantity = interest.quantity,
            certificateCode = interest.certificateCode,
            buyer = BuyerEntity.fromDomainEntity(interest.buyer),
            status = interest.status,
            createdDate = interest.createdDate
        )
    }

    fun toDomainEntity(): Interest = Interest(
        id = InterestId(id),
        quantity = quantity,
        certificateCode = certificateCode,
        buyer = buyer.toDomainEntity(),
        status = status,
        createdDate = createdDate
    )
}
