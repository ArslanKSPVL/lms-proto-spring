package com.example.lms.faculty;

import com.example.lms.assignment.Assignment;
import com.example.lms.assignment.AssignmentRepository;
import com.example.lms.assignment.AssignmentSubmissionRepository;
import com.example.lms.attendance.AttendanceRecord;
import com.example.lms.attendance.AttendanceRecordRepository;
import com.example.lms.attendance.AttendanceSession;
import com.example.lms.attendance.AttendanceSessionRepository;
import com.example.lms.assessment.AssessmentComponent;
import com.example.lms.assessment.AssessmentComponentRepository;
import com.example.lms.assessment.AssessmentType;
import com.example.lms.assessment.MarksEntry;
import com.example.lms.assessment.MarksEntryRepository;
import com.example.lms.content.Announcement;
import com.example.lms.content.AnnouncementRepository;
import com.example.lms.content.CourseContent;
import com.example.lms.content.CourseContentRepository;
import com.example.lms.course.CourseOffering;
import com.example.lms.course.CourseOfferingRepository;
import com.example.lms.course.Enrollment;
import com.example.lms.course.EnrollmentRepository;
import com.example.lms.user.CurrentUser;
import com.example.lms.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/faculty")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyController {

    private final CourseOfferingRepository offeringRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseContentRepository contentRepository;
    private final AnnouncementRepository announcementRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AssessmentComponentRepository assessmentComponentRepository;
    private final MarksEntryRepository marksEntryRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUser currentUser;

    public FacultyController(CourseOfferingRepository offeringRepository,
                             EnrollmentRepository enrollmentRepository,
                             CourseContentRepository contentRepository,
                             AnnouncementRepository announcementRepository,
                             AssignmentRepository assignmentRepository,
                             AssignmentSubmissionRepository submissionRepository,
                             AttendanceSessionRepository attendanceSessionRepository,
                             AttendanceRecordRepository attendanceRecordRepository,
                             AssessmentComponentRepository assessmentComponentRepository,
                             MarksEntryRepository marksEntryRepository,
                             FileStorageService fileStorageService,
                             CurrentUser currentUser) {
        this.offeringRepository = offeringRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.contentRepository = contentRepository;
        this.announcementRepository = announcementRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.assessmentComponentRepository = assessmentComponentRepository;
        this.marksEntryRepository = marksEntryRepository;
        this.fileStorageService = fileStorageService;
        this.currentUser = currentUser;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User faculty = currentUser.get();
        List<CourseOffering> offerings = offeringRepository.findByFaculty(faculty);
        model.addAttribute("pageTitle", "Faculty Dashboard");
        model.addAttribute("offerings", offerings);
        return "faculty/dashboard";
    }

    @GetMapping("/offering/{id}")
    public String offering(@PathVariable Long id, Model model) {
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findByOffering(offering);
        model.addAttribute("pageTitle", "Course: " + offering.getCourse().getTitle());
        model.addAttribute("offering", offering);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("contents", contentRepository.findByOfferingAndPublishedIsTrue(offering));
        model.addAttribute("announcements", announcementRepository.findByOfferingOrderByCreatedAtDesc(offering));
        model.addAttribute("assignments", assignmentRepository.findByOffering(offering));
        model.addAttribute("attendanceSessions", attendanceSessionRepository.findByOffering(offering));
        model.addAttribute("components", assessmentComponentRepository.findByOffering(offering));
        model.addAttribute("assessmentTypes", AssessmentType.values());
        return "faculty/offering";
    }

    @PostMapping("/offering/{id}/content")
    public String uploadContent(@PathVariable Long id,
                                @RequestParam String title,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) MultipartFile file,
                                @RequestParam(required = false) String link) throws IOException {
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        User faculty = currentUser.get();
        CourseContent content = new CourseContent();
        content.setOffering(offering);
        content.setUploadedBy(faculty);
        content.setTitle(title);
        content.setDescription(description);
        if (file != null && !file.isEmpty()) {
            String storedPath = fileStorageService.store(file);
            content.setFilePath(storedPath);
        } else if (StringUtils.hasText(link)) {
            content.setFilePath(link);
        }
        content.setPublished(true);
        content.setUploadedAt(LocalDateTime.now());
        contentRepository.save(content);
        return "redirect:/faculty/offering/" + id;
    }

    @PostMapping("/content/{contentId}/delete")
    public String deleteContent(@PathVariable Long contentId,
                                @RequestParam Long offeringId) {
        contentRepository.deleteById(contentId);
        return "redirect:/faculty/offering/" + offeringId;
    }

    @PostMapping("/offering/{id}/announcement")
    public String createAnnouncement(@PathVariable Long id,
                                     @RequestParam String title,
                                     @RequestParam String message) {
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        User faculty = currentUser.get();
        Announcement a = new Announcement();
        a.setOffering(offering);
        a.setAuthor(faculty);
        a.setTitle(title);
        a.setMessage(message);
        a.setCreatedAt(LocalDateTime.now());
        announcementRepository.save(a);
        return "redirect:/faculty/offering/" + id;
    }

    @PostMapping("/offering/{id}/assignment")
    public String createAssignment(@PathVariable Long id,
                                   @RequestParam String title,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String dueAt) {
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        User faculty = currentUser.get();
        Assignment assignment = new Assignment();
        assignment.setOffering(offering);
        assignment.setCreatedBy(faculty);
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setCreatedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
        return "redirect:/faculty/offering/" + id;
    }

    @PostMapping("/offering/{id}/attendance")
    public String takeAttendance(@PathVariable Long id,
                                 @RequestParam(required = false, name = "presentIds") List<Long> presentIds,
                                 @RequestParam(required = false) String topic) {
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        User faculty = currentUser.get();
        AttendanceSession session = new AttendanceSession();
        session.setOffering(offering);
        session.setTakenBy(faculty);
        session.setSessionAt(LocalDateTime.now());
        session.setTopic(topic);
        attendanceSessionRepository.save(session);

        List<Enrollment> enrollments = enrollmentRepository.findByOffering(offering);
        for (Enrollment e : enrollments) {
            AttendanceRecord r = new AttendanceRecord();
            r.setSession(session);
            r.setStudent(e.getStudent());
            boolean present = presentIds != null && presentIds.contains(e.getStudent().getId());
            r.setPresent(present);
            attendanceRecordRepository.save(r);
        }
        return "redirect:/faculty/offering/" + id;
    }

    @PostMapping("/offering/{id}/assessment-component")
    public String createAssessmentComponent(@PathVariable Long id,
                                            @RequestParam AssessmentType type,
                                            @RequestParam String name,
                                            @RequestParam double maxMarks,
                                            @RequestParam double weightage) {
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        AssessmentComponent c = new AssessmentComponent();
        c.setOffering(offering);
        c.setType(type);
        c.setName(name);
        c.setMaxMarks(maxMarks);
        c.setWeightage(weightage);
        assessmentComponentRepository.save(c);
        return "redirect:/faculty/offering/" + id;
    }

    @PostMapping("/offering/{id}/marks")
    public String enterMarks(@PathVariable Long id,
                             @RequestParam Long componentId,
                             @RequestParam Map<String, String> allParams) {
        CourseOffering offering = offeringRepository.findById(id).orElseThrow();
        AssessmentComponent component = assessmentComponentRepository.findById(componentId).orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findByOffering(offering);

        for (Enrollment e : enrollments) {
            String paramName = "marks_" + e.getStudent().getId();
            if (allParams.containsKey(paramName)) {
                String value = allParams.get(paramName);
                if (value != null && !value.isBlank()) {
                    double marks = Double.parseDouble(value);
                    MarksEntry entry = marksEntryRepository.findAll().stream()
                            .filter(m -> m.getComponent().getId().equals(componentId)
                                    && m.getStudent().getId().equals(e.getStudent().getId()))
                            .findFirst()
                            .orElseGet(MarksEntry::new);
                    entry.setComponent(component);
                    entry.setStudent(e.getStudent());
                    entry.setMarksObtained(marks);
                    marksEntryRepository.save(entry);
                }
            }
        }

        return "redirect:/faculty/offering/" + id;
    }
}

