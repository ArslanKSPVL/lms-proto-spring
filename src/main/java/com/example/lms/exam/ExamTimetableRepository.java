package com.example.lms.exam;

import com.example.lms.course.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamTimetableRepository extends JpaRepository<ExamTimetable, Long> {

    List<ExamTimetable> findByOffering(CourseOffering offering);
}

