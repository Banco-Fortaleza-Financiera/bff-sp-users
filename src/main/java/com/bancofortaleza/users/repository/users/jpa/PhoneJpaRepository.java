package com.bancofortaleza.users.repository.users.jpa;

import com.bancofortaleza.users.repository.users.entity.PhoneEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhoneJpaRepository extends JpaRepository<PhoneEntity, Integer> {

    List<PhoneEntity> findByUserId(Integer userId);

    List<PhoneEntity> findByUserIdAndStatus(Integer userId, UserEntity.Status status);

    Optional<PhoneEntity> findByIdAndUserId(Integer id, Integer userId);
}
