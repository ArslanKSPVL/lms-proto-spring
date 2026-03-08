package com.example.lms.course;

import com.example.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByOffering(CourseOffering offering);
}

