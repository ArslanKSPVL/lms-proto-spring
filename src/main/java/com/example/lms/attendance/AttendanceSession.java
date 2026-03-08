package com.example.lms.attendance;

import com.example.lms.course.CourseOffering;
import com.example.lms.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_sessions")
@Getter
@Setter
@NoArgsConstructor
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private CourseOffering offering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taken_by_id")
    private User takenBy;

    private LocalDateTime sessionAt = LocalDateTime.now();

    @Column(length = 256)
    private String topic;
}

