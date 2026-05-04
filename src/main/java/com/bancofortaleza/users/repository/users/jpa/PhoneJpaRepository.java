package com.bancofortaleza.users.repository.users.jpa;

import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface PhoneJpaRepository extends JpaRepository<PhoneEntity, Integer>, JpaSpecificationExecutor<PhoneEntity> {

    Optional<PhoneEntity> findByIdAndUserId(Integer phoneId, Integer userId);
}
