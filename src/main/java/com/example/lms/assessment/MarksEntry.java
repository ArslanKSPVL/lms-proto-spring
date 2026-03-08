package com.example.lms.assessment;

import com.example.lms.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "marks_entries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"component_id", "student_id"}))
@Getter
@Setter
@NoArgsConstructor
public class MarksEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id")
    private AssessmentComponent component;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    private double marksObtained;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;
}

