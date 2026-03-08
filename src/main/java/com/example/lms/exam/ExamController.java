package com.example.lms.exam;

import com.example.lms.assessment.AssessmentComponent;
import com.example.lms.assessment.AssessmentComponentRepository;
import com.example.lms.assessment.MarksEntry;
import com.example.lms.assessment.MarksEntryRepository;
import com.example.lms.course.CourseOffering;
import com.example.lms.course.CourseOfferingRepository;
import com.example.lms.course.Enrollment;
import com.example.lms.course.EnrollmentRepository;
import com.example.lms.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/exam")
@PreAuthorize("hasRole('EXAM_CELL')")
public class ExamController {

    private final ExamTimetableRepository timetableRepository;
    private final ExamResultRepository resultRepository;
    private final CourseOfferingRepository offeringRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssessmentComponentRepository componentRepository;
    private final MarksEntryRepository marksEntryRepository;

    public ExamController(ExamTimetableRepository timetableRepository,
                          ExamResultRepository resultRepository,
                          CourseOfferingRepository offeringRepository,
                          EnrollmentRepository enrollmentRepository,
                          AssessmentComponentRepository componentRepository,
                          MarksEntryRepository marksEntryRepository) {
        this.timetableRepository = timetableRepository;
        this.resultRepository = resultRepository;
        this.offeringRepository = offeringRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.componentRepository = componentRepository;
        this.marksEntryRepository = marksEntryRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Exam Cell Dashboard");
        model.addAttribute("timetables", timetableRepository.findAll());
        model.addAttribute("results", resultRepository.findAll());
        model.addAttribute("offerings", offeringRepository.findAll());
        return "exam/dashboard";
    }

    @PostMapping("/timetable")
    public String createTimetable(@RequestParam Long offeringId,
                                  @RequestParam String examAt,
                                  @RequestParam String venue) {
        CourseOffering offering = offeringRepository.findById(offeringId).orElseThrow();
        ExamTimetable t = new ExamTimetable();
        t.setOffering(offering);
        t.setExamAt(LocalDateTime.now());
        t.setVenue(venue);
        timetableRepository.save(t);
        return "redirect:/exam/dashboard";
    }

    @PostMapping("/results/{offeringId}/generate")
    public String generateResults(@PathVariable Long offeringId) {
        CourseOffering offering = offeringRepository.findById(offeringId).orElseThrow();
        List<AssessmentComponent> components = componentRepository.findByOffering(offering);
        List<Enrollment> enrollments = enrollmentRepository.findByOffering(offering);

        for (Enrollment e : enrollments) {
            User student = e.getStudent();
            double totalScore = 0.0;
            double totalWeight = 0.0;
            for (AssessmentComponent c : components) {
                List<MarksEntry> entries = marksEntryRepository.findByStudent(student).stream()
                        .filter(m -> m.getComponent().getId().equals(c.getId()))
                        .toList();
                if (!entries.isEmpty()) {
                    MarksEntry me = entries.get(0);
                    double normalized = (me.getMarksObtained() / c.getMaxMarks()) * c.getWeightage();
                    totalScore += normalized;
                    totalWeight += c.getWeightage();
                }
            }
            double finalMarks = totalWeight > 0 ? (totalScore / totalWeight) * 100.0 : 0.0;
            ExamResult result = new ExamResult();
            result.setStudent(student);
            result.setOffering(offering);
            result.setTotalMarks(finalMarks);
            result.setGrade(computeGrade(finalMarks));
            resultRepository.save(result);
        }

        return "redirect:/exam/dashboard";
    }

    private String computeGrade(double marks) {
        if (marks >= 90) return "O";
        if (marks >= 80) return "A+";
        if (marks >= 70) return "A";
        if (marks >= 60) return "B+";
        if (marks >= 50) return "B";
        if (marks >= 40) return "C";
        return "F";
    }
}

