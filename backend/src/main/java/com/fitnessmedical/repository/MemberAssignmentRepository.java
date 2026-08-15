package com.fitnessmedical.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitnessmedical.entity.MemberAssignment;

public interface MemberAssignmentRepository extends JpaRepository<MemberAssignment, Long> {

    Optional<MemberAssignment> findByMember_Id(Long memberId);

    List<MemberAssignment> findByPhysician_Id(Long physicianAccountId);

    List<MemberAssignment> findByTrainer_Id(Long trainerAccountId);

    boolean existsByPhysician_IdAndTrainer_Id(Long physicianAccountId, Long trainerAccountId);
}
