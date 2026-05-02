package com.bancofortaleza.users.repository.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "[user]")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "lastName", nullable = false, length = 64)
    private String lastName;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "userType", nullable = false, length = 20)
    private UserType userType = UserType.NORMAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressEntity> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhoneEntity> phones = new ArrayList<>();

    public void addAddress(AddressEntity address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void addPhone(PhoneEntity phone) {
        phones.add(phone);
        phone.setUser(this);
    }

    @PrePersist
    void prePersist() {
        if (status == null) {
            status = Status.ACTIVE;
        }
        if (userType == null) {
            userType = UserType.NORMAL;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public enum UserType {
        NORMAL,
        ADMIN
    }
}
