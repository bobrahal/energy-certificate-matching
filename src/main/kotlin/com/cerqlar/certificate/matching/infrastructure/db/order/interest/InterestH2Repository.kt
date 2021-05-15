package com.cerqlar.certificate.matching.infrastructure.db.order.interest

import com.cerqlar.certificate.matching.domain.order.interest.Interest
import com.cerqlar.certificate.matching.domain.order.interest.InterestId
import com.cerqlar.certificate.matching.domain.order.interest.InterestRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InterestH2Repository(private val crudRepository: InterestCRUDRepository) :
    InterestRepository {

    override fun fetchInterestById(id: InterestId): Interest? {
        return crudRepository.findById(id.id).orElse(null)?.toDomainEntity()
    }

    override fun save(interest: Interest): Interest {
        return crudRepository.save(InterestEntity.fromDomainEntity(interest)).toDomainEntity()
    }

    @Transactional
    override fun update(status: String, id: Long) = crudRepository.updateStatus(status, id)
}