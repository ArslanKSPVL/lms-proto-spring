package com.example.lms.exam;

import com.example.lms.course.CourseOffering;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_timetables")
@Getter
@Setter
@NoArgsConstructor
public class ExamTimetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private CourseOffering offering;

    private LocalDateTime examAt;

    @Column(length = 128)
    private String venue;
}

