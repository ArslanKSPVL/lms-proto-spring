package com.example.lms.exam;

import com.example.lms.course.CourseOffering;
import com.example.lms.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_results",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "offering_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private CourseOffering offering;

    private double totalMarks;

    @Column(length = 4)
    private String grade; // e.g. A+, B, etc.
}

