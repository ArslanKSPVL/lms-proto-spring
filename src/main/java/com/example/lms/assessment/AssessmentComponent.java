package com.example.lms.assessment;

import com.example.lms.course.CourseOffering;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assessment_components")
@Getter
@Setter
@NoArgsConstructor
public class AssessmentComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private CourseOffering offering;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AssessmentType type;

    @Column(nullable = false, length = 64)
    private String name; // e.g. CIA-1, Mid-term

    @Column(nullable = false)
    private double maxMarks;

    @Column(nullable = false)
    private double weightage; // percentage toward final grade
}

