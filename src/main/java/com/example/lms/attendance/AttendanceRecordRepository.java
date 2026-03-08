package com.example.lms.attendance;

import com.example.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByStudent(User student);

    List<AttendanceRecord> findBySession(AttendanceSession session);
}

