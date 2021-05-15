package com.cerqlar.certificate.matching.infrastructure.db.order.book

import com.cerqlar.certificate.matching.contants.MatchType
import com.cerqlar.certificate.matching.domain.order.book.OrderBook
import com.cerqlar.certificate.matching.domain.order.book.OrderBookId
import com.cerqlar.certificate.matching.infrastructure.db.order.bundle.CertificateBundleEntity
import com.cerqlar.certificate.matching.infrastructure.db.order.interest.InterestEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "order_book")
data class OrderBookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long,
    @ManyToOne
    @JoinColumn(name = "interest_id")
    var interestEntity: InterestEntity,
    @ManyToOne
    @JoinColumn(name = "certificate_bundle_id")
    var certificateBundleEntity: CertificateBundleEntity,
    var quantity: Int,
    @Enumerated(EnumType.STRING)
    var matchType: MatchType,
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    var matchedDate: Date = Date()
) {
    companion object {
        fun fromDomainEntity(orderBook: OrderBook): OrderBookEntity = OrderBookEntity(
            id = orderBook.id.id,
            interestEntity = InterestEntity.fromDomainEntity(orderBook.interest),
            certificateBundleEntity = CertificateBundleEntity.fromDomainEntity(orderBook.certificateBundle),
            quantity = orderBook.quantity,
            matchType = orderBook.matchType,
            matchedDate = orderBook.matchedDate
        )
    }

    fun toDomainEntity(): OrderBook = OrderBook(
        id = OrderBookId(id),
        interest = interestEntity.toDomainEntity(),
        certificateBundle = certificateBundleEntity.toDomainEntity(),
        quantity = quantity,
        matchType = matchType,
        matchedDate = matchedDate,
    )
}