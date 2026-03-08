package com.example.lms.assignment;

import com.example.lms.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "student_id"}))
@Getter
@Setter
@NoArgsConstructor
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @Column(length = 512)
    private String filePath;

    private LocalDateTime submittedAt = LocalDateTime.now();
}

