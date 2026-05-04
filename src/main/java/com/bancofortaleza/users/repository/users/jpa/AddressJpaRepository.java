package com.bancofortaleza.users.repository.users.jpa;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, Integer>,JpaSpecificationExecutor<AddressEntity> {

    Optional<AddressEntity> findByIdAndUserId(Integer id, Integer userId);
}
