package com.cerqlar.certificate.matching.service

import com.cerqlar.certificate.matching.contants.MatchType
import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.agent.buyer.Buyer
import com.cerqlar.certificate.matching.domain.agent.buyer.BuyerId
import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.agent.seller.SellerId
import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.domain.cert.EnergySourceId
import com.cerqlar.certificate.matching.domain.cert.EnergySourceRepository
import com.cerqlar.certificate.matching.domain.order.book.OrderBook
import com.cerqlar.certificate.matching.domain.order.book.OrderBookId
import com.cerqlar.certificate.matching.domain.order.book.OrderBookRepository
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleId
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleRepository
import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.domain.order.interest.InterestId
import com.cerqlar.certificate.matching.domain.order.interest.InterestRepository
import com.cerqlar.certificate.matching.exceptions.OrderNotMatchedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class OrderBookServiceTest {

    @Mock
    private lateinit var interestRepository: InterestRepository
    @Mock
    private lateinit var certificateBundleRepository: CertificateBundleRepository
    @Mock
    private lateinit var orderBookRepository: OrderBookRepository
    @Mock
    private lateinit var energySourceRepository: EnergySourceRepository
    @InjectMocks
    private lateinit var underTest: OrderBookService

    private val dummySeller = Seller(SellerId(1L), code = "S0001", name = "Dummy Seller")
    private val dummyEnergySource = EnergySource(EnergySourceId(1L), "WTR", "Water")
    private val dummyBuyer = Buyer(BuyerId(1L), code = "B0001", name = "Dummy Buyer")

    private val certificateBundle: CertificateBundle = CertificateBundle(
        CertificateBundleId(1L),
        dummyEnergySource,
        50,
        "Issuer",
        Date(),
        dummySeller,
        Status.PENDING
    )

    private val certificateBundle1: CertificateBundle = CertificateBundle(
        CertificateBundleId(2L),
        dummyEnergySource,
        75,
        "Issuer",
        Date(),
        dummySeller,
        Status.PENDING
    )

    private var interest: Interest = Interest(
        InterestId(1L), "WTR", 100, dummyBuyer,
        Status.PENDING, Date()
    )

    private val orderBook: OrderBook =
        OrderBook(OrderBookId(0L), interest, certificateBundle1, 75, MatchType.FULL, Date())
    private val orderBook1: OrderBook =
        OrderBook(OrderBookId(0L), interest, certificateBundle, 25, MatchType.PARTIAL, Date())

    @Test
    fun `should match interest and certificate bundle`() {

        // given
        given(certificateBundleRepository.findAllByEnergySourceAndStatus(dummyEnergySource, Status.PENDING)).willReturn(
            listOf(certificateBundle1, certificateBundle))
        given(energySourceRepository.fetchEnergySourceByCode(dummyEnergySource.code)).willReturn(dummyEnergySource)

        // when
        val output = underTest.executeMatchInterest(interest)

        // then
        assertThat(output!!.orderBooks[0]).usingRecursiveComparison()
            .ignoringFields("matchedDate") // milliseconds difference
            .isEqualTo(
                orderBook
            )
        then(orderBookRepository).should().save(output!!.orderBooks[0])
        assertThat(output.orderBooks[1]).usingRecursiveComparison()
            .ignoringFields("matchedDate") // milliseconds difference
            .isEqualTo(
                orderBook1
            )
        then(orderBookRepository).should().save(output.orderBooks[1])
    }

    @Test
    fun `should fail to match interest when certificate code is not found`() {

        interest = Interest(
            InterestId(1L), "HDRGN", 100, dummyBuyer,
            Status.PENDING, Date()
        )

        // when/then
        assertThrows(OrderNotMatchedException::class.java) {
            underTest.executeMatchInterest(interest)
        }
    }

    @Test
    fun `should fail to match interest when no combination of certificate bundles found`() {

        // given
        given(energySourceRepository.fetchEnergySourceByCode(dummyEnergySource.code)).willReturn(dummyEnergySource)

        // when/then
        assertThrows(OrderNotMatchedException::class.java) {
            underTest.executeMatchInterest(interest)
        }
    }
}