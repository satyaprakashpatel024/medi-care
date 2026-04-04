package com.care.medi.repository;

import com.care.medi.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // ── Queries ───────────────────────────────
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultByUserId(Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
    List<Address> findByUserId(Long userId);
}