package com.bancofortaleza.users.repository.users;

import com.bancofortaleza.users.repository.users.entity.AddressEntity;
import com.bancofortaleza.users.repository.users.entity.UserEntity;
import com.bancofortaleza.users.repository.users.jpa.AddressJpaRepository;
import com.bancofortaleza.users.utils.PaginationUtils;
import com.bancofortaleza.users.utils.SpecificationUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AddressRepository {

    private final AddressJpaRepository addressesJpaRepository;
    

    @Transactional
    public AddressEntity createUserAddress(AddressEntity address) {
        return addressesJpaRepository.save(address);
    }


    public Page<AddressEntity> listUserAddresses(Integer userId, Integer xPage, Integer xPageSize, String search, UserEntity.Status status) {
        Pageable pageable = PaginationUtils.fromHeaders(xPage, xPageSize, Sort.by(Sort.Direction.ASC, "id"));

        return addressesJpaRepository.findAll(buildListUserAddressesSpecification(userId, search, status), pageable);
    }

    public AddressEntity getUserAddressById(Integer userId, Integer addressId) {
        return addressesJpaRepository.findByIdAndUserId(addressId, userId).orElse(null);
    }

    @Transactional
    public AddressEntity updateAddressStatus(Integer userId, Integer id, UserEntity.Status status) {
        return addressesJpaRepository.findByIdAndUserId(id, userId)
                .map(address -> {
                    address.setStatus(status);
                    return addressesJpaRepository.save(address);
                }).orElse(null);
    }

    private Specification<AddressEntity> buildListUserAddressesSpecification(
            Integer userId,
            String search,
            UserEntity.Status status
    ) {
        return SpecificationUtils.<AddressEntity>equalIfNotNull("user.id", userId)
                .and(SpecificationUtils.equalIfNotNull("status", status))
                .and(SpecificationUtils.containsIgnoreCase(search, "address", "typeAddress"));
    }

    
}
