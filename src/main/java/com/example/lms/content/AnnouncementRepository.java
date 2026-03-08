package com.example.lms.content;

import com.example.lms.course.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByOfferingOrderByCreatedAtDesc(CourseOffering offering);
}

