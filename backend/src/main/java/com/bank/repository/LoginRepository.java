package com.bank.repository;

import com.bank.entity.LoginEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<LoginEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LoginEntity l WHERE l.pinLookupHash = :hash")
    Optional<LoginEntity> findByPinLookupHashForUpdate(@Param("hash") String hash);

    Optional<LoginEntity> findByPinLookupHash(String hash);
}
