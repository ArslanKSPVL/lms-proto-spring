package com.example.lms.exam;

import com.example.lms.course.CourseOffering;
import com.example.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    List<ExamResult> findByStudent(User student);

    List<ExamResult> findByOffering(CourseOffering offering);
}

