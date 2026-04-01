package com.care.medi.repository;

import com.care.medi.entity.OtpTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpTableRepository extends JpaRepository<OtpTable, Long> {
}