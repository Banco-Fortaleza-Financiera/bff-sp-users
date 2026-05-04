package com.bancofortaleza.users.repository.users;

import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.repository.users.jpa.PhoneJpaRepository;
import com.bancofortaleza.users.utils.PaginationUtils;
import com.bancofortaleza.users.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PhoneRepository {

    private final PhoneJpaRepository phonesJpaRepository;

    @Transactional
    public PhoneEntity createUserPhone(PhoneEntity phone) {
        return phonesJpaRepository.save(phone);
    }

    public Page<PhoneEntity> listUserPhones(Integer userId, Integer xPage, Integer xPageSize, String search, UserEntity.Status status) {
        Pageable pageable = PaginationUtils.fromHeaders(xPage, xPageSize, Sort.by(Sort.Direction.ASC, "id"));

        return phonesJpaRepository.findAll(buildListUserPhonesSpecification(userId, search, status), pageable);
    }

    public PhoneEntity getUserPhoneById(Integer userId, Integer phoneId) {
        return phonesJpaRepository.findByIdAndUserId(phoneId, userId).orElse(null);
    }

    @Transactional
    public PhoneEntity updatePhoneStatus(Integer userId, Integer id, UserEntity.Status status) {
        return phonesJpaRepository.findByIdAndUserId(id, userId)
                .map(phone -> {
                    phone.setStatus(status);
                    return phonesJpaRepository.save(phone);
                }).orElse(null);
    }

    private Specification<PhoneEntity> buildListUserPhonesSpecification(
            Integer userId,
            String search,
            UserEntity.Status status
    ) {
        return SpecificationUtils.<PhoneEntity>equalIfNotNull("user.id", userId)
                .and(SpecificationUtils.equalIfNotNull("status", status))
                .and(SpecificationUtils.containsIgnoreCase(search, "phone", "typePhone"));
    }
}
