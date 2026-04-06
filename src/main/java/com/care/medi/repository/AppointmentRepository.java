package com.care.medi.repository;

import com.care.medi.entity.Appointment;
import com.care.medi.entity.AppointmentStatus;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"patient","department","doctor","prescription"})
    Page<Appointment> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"patient","department","doctor","prescription"})
    Page<Appointment> findByStatus(AppointmentStatus status,Pageable pageable);
}