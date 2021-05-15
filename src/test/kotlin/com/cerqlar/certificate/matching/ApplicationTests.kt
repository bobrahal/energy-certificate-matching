package com.cerqlar.certificate.matching

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.agent.buyer.Buyer
import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleId
import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.domain.order.interest.InterestId
import com.cerqlar.certificate.matching.infrastructure.db.agent.buyer.BuyerCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.agent.buyer.BuyerEntity
import com.cerqlar.certificate.matching.infrastructure.db.agent.seller.SellerCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.agent.seller.SellerEntity
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.cert.EnergySourceEntity
import com.cerqlar.certificate.matching.infrastructure.db.order.book.OrderBookCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.order.bundle.CertificateBundleCRUDRepository
import com.cerqlar.certificate.matching.infrastructure.db.order.interest.InterestCRUDRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Integration Tests for certificate matching service")
class ApplicationTests(
    @Autowired private val restTemplate: TestRestTemplate,
    @Autowired private val sellerCRUDRepository: SellerCRUDRepository,
    @Autowired private val buyerCRUDRepository: BuyerCRUDRepository,
    @Autowired private val energySourceCRUDRepository: EnergySourceCRUDRepository,
    @Autowired private val interestCRUDRepository: InterestCRUDRepository,
    @Autowired private val certificateBundleCRUDRepository: CertificateBundleCRUDRepository,
    @Autowired private val orderBookCRUDRepository: OrderBookCRUDRepository
) {

    private lateinit var sellerEntity: SellerEntity
    private lateinit var energySourceEntity: EnergySourceEntity
    private lateinit var buyerEntity: BuyerEntity

    @BeforeEach
    fun beforeTest() {
        orderBookCRUDRepository.deleteAll()
        certificateBundleCRUDRepository.deleteAll()
        interestCRUDRepository.deleteAll()
        sellerCRUDRepository.deleteAll()
        buyerCRUDRepository.deleteAll()
        energySourceCRUDRepository.deleteAll()

        sellerEntity = sellerCRUDRepository.save(dummySeller().copy(id = 2L, code = "S0002"))
        energySourceEntity = energySourceCRUDRepository.save(dummyEnergySource().copy(id = 2L, code = "WTR", name = "Water"))
        buyerEntity = buyerCRUDRepository.save(dummyBuyer().copy(id = 2L, code = "B0002"))
    }

    @Test
    @DirtiesContext
    fun `should create certificate bundle`() {
        // given
        val quantity: Int = 50
        val expectedBody =
            """{"message":"Sell Order (Certificate Bundle) for 50 Water certificates successfully created for seller S0002","code":"Success"}"""

        // when
        val response = this.restTemplate.postForEntity(
            "/add-bundle",
            buildCreateSellOrderRequest(
                sellerEntity.toDomainEntity(), energySourceEntity.toDomainEntity(), quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )

        // then
        assertThat(response.body).isEqualTo(expectedBody)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        // expected
        val expectedCount: Long = 1
        // when
        val count: Long = certificateBundleCRUDRepository.count()
        // then
        assertThat(count).isEqualTo(expectedCount)
    }

    @Test
    @DirtiesContext
    fun `should fail to create certificate bundle for unknown seller`() {

        // given
        val quantity: Int = 50
        val expectedBody = """{"message":"Unable to create sell order. Seller S0003 not found","code":"ERR-1"}"""

        // when
        val response = this.restTemplate.postForEntity(
            "/add-bundle",
            buildCreateSellOrderRequest(
                dummySeller().copy(id = 3L, code = "S0003").toDomainEntity(),
                energySourceEntity.toDomainEntity(),
                quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )

        // then
        assertThat(response.body).isEqualTo(expectedBody)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        // expected
        val expectedCount: Long = 0
        // when
        val count: Long = certificateBundleCRUDRepository.count()
        // then
        assertThat(count).isEqualTo(expectedCount)
    }

    @Test
    @DirtiesContext
    fun `should fail to create certificate bundle with zero quantity`() {

        // given
        val quantity: Int = 0
        val expectedBody = """{"message":"Unable to create order. Quantity should be greater than zero","code":"ERR-4"}"""

        // when
        val response = this.restTemplate.postForEntity(
            "/add-bundle",
            buildCreateSellOrderRequest(
                sellerEntity.toDomainEntity(),
                energySourceEntity.toDomainEntity(),
                quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )

        // then
        assertThat(response.body).isEqualTo(expectedBody)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        // expected
        val expectedCount: Long = 0
        // when
        val count: Long = certificateBundleCRUDRepository.count()
        // then
        assertThat(count).isEqualTo(expectedCount)
    }

    @Test
    @DirtiesContext
    fun `should fail to create interest with zero quantity`() {

        // given
        val quantity: Int = 0
        val expectedBody = """{"message":"Unable to create order. Quantity should be greater than zero","code":"ERR-4"}"""

        // when
        val response = this.restTemplate.postForEntity(
            "/add-interest",
            buildCreateBuyOrderRequest(
                buyerEntity.toDomainEntity(), energySourceEntity.code, quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )

        // then
        assertThat(response.body).isEqualTo(expectedBody)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        // expected
        val expectedCount: Long = 0
        // when
        val count: Long = certificateBundleCRUDRepository.count()
        // then
        assertThat(count).isEqualTo(expectedCount)
    }

    @Test
    @DirtiesContext
    fun `should fail to create certificate bundle for unknown energy source`() {

        // given
        val quantity: Int = 50
        val expectedBody = """{"message":"Unable to create order. Energy source HDRGN not found","code":"ERR-3"}"""

        // when
        val response = this.restTemplate.postForEntity(
            "/add-bundle",
            buildCreateSellOrderRequest(
                sellerEntity.toDomainEntity(),
                dummyEnergySource().copy(id = 5L, code = "HDRGN", name = "Hydrogen").toDomainEntity(),
                quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )

        // then
        assertThat(response.body).isEqualTo(expectedBody)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        // expected
        val expectedCount: Long = 0
        // when
        val count: Long = certificateBundleCRUDRepository.count()
        // then
        assertThat(count).isEqualTo(expectedCount)
    }

    @Test
    @DirtiesContext
    fun `should create interest but fail to match`() {
        // given
        val quantity: Int = 100
        val expectedBody =
            """{"message":"Order not matched: no combination of certificate bundles found that match interest","code":"ERR-4"}"""

        // when
        val response = this.restTemplate.postForEntity(
            "/add-interest",
            buildCreateBuyOrderRequest(
                buyerEntity.toDomainEntity(), energySourceEntity.code, quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )

        // then
        assertThat(response.body).isEqualTo(expectedBody)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        // interest should be created
        // expected
        var expectedCount: Long = 1
        // when
        var interests: List<Interest> = interestCRUDRepository.findAll().map { it.toDomainEntity() }
        // then
        assertThat(interests.size).isEqualTo(expectedCount)
        assertThat(interests.elementAt(0).status).isEqualTo(Status.PENDING)

        // expected
        expectedCount = 0
        // when
        val count: Long = orderBookCRUDRepository.count()
        // then
        assertThat(count).isEqualTo(expectedCount)
    }

    @Test
    @DirtiesContext
    fun `should create interest and match`() {
        // given
        val quantity: Int = 100
        val expectedBody =
            """{"message":"Buy Order (Interest) for 100 WTR certificates successfully created for buyer B0002","code":"Success"}"""

        // when
        // create certificate bundle with same quantity and energy source
        this.restTemplate.postForEntity(
            "/add-bundle",
            buildCreateSellOrderRequest(
                sellerEntity.toDomainEntity(),
                energySourceEntity.toDomainEntity(),
                quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )
        val response = this.restTemplate.postForEntity(
            "/add-interest",
            buildCreateBuyOrderRequest(
                buyerEntity.toDomainEntity(), energySourceEntity.code, quantity
            ),
            String::class.java,
            emptyMap<String, String>()
        )

        // then
        assertThat(response.body).isEqualTo(expectedBody)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        // interest should be created
        // expected
        var expectedCount: Long = 1
        // when
        var interests: List<Interest> = interestCRUDRepository.findAll().map { it.toDomainEntity() }
        // then
        assertThat(interests.size).isEqualTo(expectedCount)
        assertThat(interests.elementAt(0).status).isEqualTo(Status.CLOSED)

        // when
        val count: Long = orderBookCRUDRepository.count()
        // then
        assertThat(count).isEqualTo(expectedCount)
    }

    private fun dummySeller(): SellerEntity {
        return SellerEntity(id = 1L, code = "S0001", name = "Dummy Seller")
    }

    private fun dummyBuyer(): BuyerEntity {
        return BuyerEntity(id = 1L, code = "B0001", name = "Dummy Buyer")
    }

    private fun dummyEnergySource(): EnergySourceEntity {
        return EnergySourceEntity(id = 1L, code = "SLR", name = "Solar")
    }

    private fun buildCreateSellOrderRequest(
        seller: Seller,
        energySource: EnergySource,
        quantity: Int
    ): HttpEntity<*> {
        val requestHeaders = HttpHeaders()
        val certificateBundle =
            CertificateBundle(CertificateBundleId(0), energySource, quantity, "Issuer", Date(), seller)
        return HttpEntity<Any>(certificateBundle, requestHeaders)
    }

    private fun buildCreateBuyOrderRequest(
        buyer: Buyer,
        certificateCode: String,
        quantity: Int
    ): HttpEntity<*> {
        val requestHeaders = HttpHeaders()
        val interest =
            Interest(InterestId(0), certificateCode, quantity, buyer)
        return HttpEntity<Any>(interest, requestHeaders)
    }
}
