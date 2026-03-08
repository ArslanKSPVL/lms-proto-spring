package com.example.lms.student;

import com.example.lms.assignment.Assignment;
import com.example.lms.assignment.AssignmentRepository;
import com.example.lms.assignment.AssignmentSubmission;
import com.example.lms.assignment.AssignmentSubmissionRepository;
import com.example.lms.attendance.AttendanceRecord;
import com.example.lms.attendance.AttendanceRecordRepository;
import com.example.lms.course.CourseOffering;
import com.example.lms.course.CourseOfferingRepository;
import com.example.lms.course.Enrollment;
import com.example.lms.course.EnrollmentRepository;
import com.example.lms.course.EnrollmentStatus;
import com.example.lms.assessment.MarksEntry;
import com.example.lms.assessment.MarksEntryRepository;
import com.example.lms.content.AnnouncementRepository;
import com.example.lms.content.CourseContentRepository;
import com.example.lms.exam.ExamResultRepository;
import com.example.lms.exam.ExamTimetableRepository;
import com.example.lms.user.CurrentUser;
import com.example.lms.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final CurrentUser currentUser;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseOfferingRepository offeringRepository;
    private final CourseContentRepository contentRepository;
    private final AnnouncementRepository announcementRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final MarksEntryRepository marksEntryRepository;
    private final ExamTimetableRepository examTimetableRepository;
    private final ExamResultRepository examResultRepository;

    public StudentController(CurrentUser currentUser,
                             EnrollmentRepository enrollmentRepository,
                             CourseOfferingRepository offeringRepository,
                             CourseContentRepository contentRepository,
                             AnnouncementRepository announcementRepository,
                             AssignmentRepository assignmentRepository,
                             AssignmentSubmissionRepository submissionRepository,
                             AttendanceRecordRepository attendanceRecordRepository,
                             MarksEntryRepository marksEntryRepository,
                             ExamTimetableRepository examTimetableRepository,
                             ExamResultRepository examResultRepository) {
        this.currentUser = currentUser;
        this.enrollmentRepository = enrollmentRepository;
        this.offeringRepository = offeringRepository;
        this.contentRepository = contentRepository;
        this.announcementRepository = announcementRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.marksEntryRepository = marksEntryRepository;
        this.examTimetableRepository = examTimetableRepository;
        this.examResultRepository = examResultRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User student = currentUser.get();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        model.addAttribute("pageTitle", "Student Dashboard");
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("assignments", getAssignmentsForStudent(student, enrollments));
        model.addAttribute("notifications", getAnnouncementsForStudent(enrollments));
        return "student/dashboard";
    }

    @GetMapping("/offerings")
    public String availableOfferings(Model model) {
        model.addAttribute("pageTitle", "Available Courses");
        model.addAttribute("offerings", offeringRepository.findAll());
        return "student/offerings";
    }

    @PostMapping("/offerings/{id}/enroll")
    public String enroll(@PathVariable Long id) {
        User student = currentUser.get();
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setOffering(offering);
        enrollment.setStatus(EnrollmentStatus.REGISTERED);
        enrollmentRepository.save(enrollment);
        return "redirect:/student/courses";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        User student = currentUser.get();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        model.addAttribute("pageTitle", "My Courses");
        model.addAttribute("enrollments", enrollments);
        return "student/courses";
    }

    @GetMapping("/courses/{enrollmentId}")
    public String courseDetail(@PathVariable Long enrollmentId, Model model) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        CourseOffering offering = enrollment.getOffering();
        model.addAttribute("pageTitle", "Course: " + offering.getCourse().getTitle());
        model.addAttribute("enrollment", enrollment);
        model.addAttribute("contents", contentRepository.findByOfferingAndPublishedIsTrue(offering));
        model.addAttribute("announcements", announcementRepository.findByOfferingOrderByCreatedAtDesc(offering));
        model.addAttribute("assignments", assignmentRepository.findByOffering(offering));
        return "student/course-detail";
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public String submitAssignment(@PathVariable Long assignmentId,
                                   @RequestParam MultipartFile file) throws IOException {
        User student = currentUser.get();
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        // For simplicity we just store original filename; integrate storage if needed
        submission.setFilePath(file.getOriginalFilename());
        submission.setSubmittedAt(LocalDateTime.now());
        submissionRepository.save(submission);
        return "redirect:/student/courses";
    }

    @GetMapping("/attendance")
    public String attendance(Model model) {
        User student = currentUser.get();
        List<AttendanceRecord> records = attendanceRecordRepository.findByStudent(student);
        Map<CourseOffering, List<AttendanceRecord>> byOffering = records.stream()
                .collect(Collectors.groupingBy(r -> r.getSession().getOffering()));
        model.addAttribute("pageTitle", "Attendance");
        model.addAttribute("attendanceByOffering", byOffering);
        return "student/attendance";
    }

    @GetMapping("/marks")
    public String marks(Model model) {
        User student = currentUser.get();
        List<MarksEntry> entries = marksEntryRepository.findByStudent(student);
        model.addAttribute("pageTitle", "Internal Marks");
        model.addAttribute("marksEntries", entries);
        return "student/marks";
    }

    @GetMapping("/exams")
    public String exams(Model model) {
        User student = currentUser.get();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        List<CourseOffering> offerings = enrollments.stream()
                .map(Enrollment::getOffering)
                .toList();
        model.addAttribute("pageTitle", "Examinations");
        model.addAttribute("offerings", offerings);
        model.addAttribute("timetables", examTimetableRepository.findAll());
        model.addAttribute("results", examResultRepository.findByStudent(student));
        return "student/exams";
    }

    private List<Assignment> getAssignmentsForStudent(User student, List<Enrollment> enrollments) {
        List<Assignment> result = new ArrayList<>();
        for (Enrollment e : enrollments) {
            result.addAll(assignmentRepository.findByOffering(e.getOffering()));
        }
        return result;
    }

    private List<Object> getAnnouncementsForStudent(List<Enrollment> enrollments) {
        List<Object> result = new ArrayList<>();
        enrollments.forEach(e ->
                result.addAll(announcementRepository.findByOfferingOrderByCreatedAtDesc(e.getOffering())));
        return result;
    }
}

