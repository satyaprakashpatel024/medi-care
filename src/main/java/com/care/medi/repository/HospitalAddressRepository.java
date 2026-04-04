package com.care.medi.repository;

import com.care.medi.entity.HospitalAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalAddressRepository extends JpaRepository<HospitalAddress, Long> {
}
