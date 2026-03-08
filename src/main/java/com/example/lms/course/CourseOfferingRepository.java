package com.example.lms.course;

import com.example.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    List<CourseOffering> findByFaculty(User faculty);
}

