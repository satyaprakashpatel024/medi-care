package com.care.medi.repository;

import com.care.medi.entity.Hospital;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    @Query("SELECT h FROM Hospital h JOIN FETCH h.hospitalDepartments hd JOIN FETCH hd.department d WHERE h.id = :id")
    Optional<Hospital> findByIdWithDepartments(Long id);

    @Query("SELECT h FROM Hospital h JOIN FETCH h.addresses a JOIN FETCH h.hospitalDepartments hd JOIN FETCH hd.department d")
    Set<Hospital> findAllWithAddressAndDepartments();

    @Override @NonNull
    @EntityGraph(attributePaths = {"addresses", "hospitalDepartments",""})
    Page<Hospital> findAll(@NonNull Pageable pageable);
}