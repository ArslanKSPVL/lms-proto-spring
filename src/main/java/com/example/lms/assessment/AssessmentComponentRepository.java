package com.example.lms.assessment;

import com.example.lms.course.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentComponentRepository extends JpaRepository<AssessmentComponent, Long> {

    List<AssessmentComponent> findByOffering(CourseOffering offering);
}

