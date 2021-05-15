package com.cerqlar.certificate.matching.infrastructure.db.order.book

import com.cerqlar.certificate.matching.contants.MatchType
import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.agent.buyer.Buyer
import com.cerqlar.certificate.matching.domain.agent.buyer.BuyerId
import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.agent.seller.SellerId
import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.domain.cert.EnergySourceId
import com.cerqlar.certificate.matching.domain.order.book.OrderBook
import com.cerqlar.certificate.matching.domain.order.book.OrderBookId
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleId
import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.domain.order.interest.InterestId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.only
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class OrderBookH2RepositoryTest{

    @Mock
    private lateinit var repository: OrderBookCRUDRepository

    @InjectMocks
    private lateinit var underTest: OrderBookH2Repository

    @Test
    fun `should save order book`()
    {
        // given
        val inputOutput = OrderBook(
            OrderBookId(1L),
            Interest(InterestId(1L), "WTR", 50, Buyer(BuyerId(1L), code = "B0001", name = "Dummy Buyer"), Status.CLOSED, Date()),
            CertificateBundle(
                CertificateBundleId(1L),
                EnergySource(EnergySourceId(1L), "WTR", "Water"),
                50,
                "Issuer",
                Date(),
                Seller(SellerId(1L), code = "S0001", name = "Dummy Seller"),
                Status.CLOSED
            ),
            50,
            MatchType.FULL,
            Date()
        )

        val dbEntity = OrderBookEntity.fromDomainEntity(inputOutput)
        given(repository.save(dbEntity)).willReturn(dbEntity)

        // when
        val output = underTest.save(inputOutput)

        // then
        then(repository).should(only()).save(dbEntity)
        assertThat(output).usingRecursiveComparison().isEqualTo(inputOutput)
    }
}