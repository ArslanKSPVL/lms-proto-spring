package com.example.lms.assignment;

import com.example.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    List<AssignmentSubmission> findByStudent(User student);

    List<AssignmentSubmission> findByAssignment(Assignment assignment);
}

