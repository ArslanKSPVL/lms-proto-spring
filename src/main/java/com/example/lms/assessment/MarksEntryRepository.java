package com.example.lms.assessment;

import com.example.lms.course.CourseOffering;
import com.example.lms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarksEntryRepository extends JpaRepository<MarksEntry, Long> {

    List<MarksEntry> findByStudent(User student);

    @Query("select m from MarksEntry m where m.component.offering = :offering")
    List<MarksEntry> findByOffering(@Param("offering") CourseOffering offering);
}

