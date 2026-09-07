package com.care.medi.repository;

import com.care.medi.entity.OtpTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTableRepository extends JpaRepository<OtpTable, Long> {

    Optional<OtpTable> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<OtpTable> findByEmailAndOtp(String email, String otp);

    void deleteByEmail(String email);
}