package com.example.lms.attendance;

import com.example.lms.course.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    List<AttendanceSession> findByOffering(CourseOffering offering);
}

