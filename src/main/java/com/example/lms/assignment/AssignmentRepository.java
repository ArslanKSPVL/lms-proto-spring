package com.example.lms.assignment;

import com.example.lms.course.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByOffering(CourseOffering offering);
}

