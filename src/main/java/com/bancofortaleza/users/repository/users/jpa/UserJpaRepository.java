package com.bancofortaleza.users.repository.users.jpa;

import com.bancofortaleza.users.repository.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {
}
