package com.cerqlar.certificate.matching.infrastructure.db.order.bundle

import com.cerqlar.certificate.matching.contants.Status
import com.cerqlar.certificate.matching.domain.agent.seller.Seller
import com.cerqlar.certificate.matching.domain.agent.seller.SellerId
import com.cerqlar.certificate.matching.domain.cert.EnergySource
import com.cerqlar.certificate.matching.domain.cert.EnergySourceId
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundle
import com.cerqlar.certificate.matching.domain.order.bundle.CertificateBundleId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.only
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Sort
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class CertificateBundleH2RepositoryTest {

    @Mock
    private lateinit var repository: CertificateBundleCRUDRepository

    @InjectMocks
    private lateinit var underTest: CertificateBundleH2Repository

    val certificateBundle: CertificateBundle

    init {
        certificateBundle = CertificateBundle(
            CertificateBundleId(1L),
            EnergySource(EnergySourceId(1L), "WTR", "Water"),
            50,
            "Issuer",
            Date(),
            Seller(SellerId(1L), code = "S0001", name = "Dummy Seller"),
            Status.PENDING
        )
    }

    @Test
    fun `should save certificate bundle`() {

        //given
        val dbEntity = CertificateBundleEntity.fromDomainEntity(certificateBundle)
        given(repository.save(dbEntity)).willReturn(dbEntity)

        // when
        val output = underTest.save(certificateBundle)

        // then
        then(repository).should(only()).save(dbEntity)
        assertThat(output).usingRecursiveComparison().isEqualTo(certificateBundle)
    }

    @Test
    fun `should fetch certificate bundle by id`() {
        // given
        val dbEntity = CertificateBundleEntity.fromDomainEntity(certificateBundle)
        given(repository.findById(dbEntity.id)).willReturn(
            Optional.of(
                dbEntity
            )
        )
        // when
        val output = underTest.fetchCertificateBundleById(certificateBundle.id)

        // then
        assertThat(output).usingRecursiveComparison().isEqualTo(
            certificateBundle
        )
    }

    @Test
    fun `should find all pending certificate bundles by energy source`() {

        // given
        val dbEntity = CertificateBundleEntity.fromDomainEntity(certificateBundle)

        given(
            repository.findAllByEnergySourceAndStatus(
                dbEntity.energySource,
                dbEntity.status,
                Sort.by(Sort.Direction.DESC, "quantity")
            )
        ).willReturn(listOf(dbEntity))

        // when
        val output = underTest.findAllByEnergySourceAndStatus(
            certificateBundle.energySource,
            Status.PENDING
        )

        // then
        assertThat(output.size).isEqualTo(1)
    }
}