package com.example.lms.content;

import com.example.lms.course.CourseOffering;
import com.example.lms.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_contents")
@Getter
@Setter
@NoArgsConstructor
public class CourseContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private CourseOffering offering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(length = 512)
    private String description;

    // For simplicity store path or URL. Could be file system or external link.
    @Column(length = 512)
    private String filePath;

    private boolean published = false;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}

