package com.example.lms.admin;

import com.example.lms.course.Course;
import com.example.lms.course.CourseOffering;
import com.example.lms.course.CourseOfferingRepository;
import com.example.lms.course.CourseRepository;
import com.example.lms.security.RoleName;
import com.example.lms.user.User;
import com.example.lms.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CourseRepository courseRepository;
    private final CourseOfferingRepository offeringRepository;

    public AdminController(UserService userService,
                           CourseRepository courseRepository,
                           CourseOfferingRepository offeringRepository) {
        this.userService = userService;
        this.courseRepository = courseRepository;
        this.offeringRepository = offeringRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute("courseCount", courseRepository.count());
        model.addAttribute("offeringCount", offeringRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("pageTitle", "Manage Users");
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", RoleName.values());
        return "admin/users";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String username,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam("role") RoleName role,
                             Model model) {
        try {
            userService.createUser(username, fullName, email, password, Set.of(role));
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageTitle", "Manage Users");
            model.addAttribute("users", userService.findAll());
            model.addAttribute("roles", RoleName.values());
            model.addAttribute("error", ex.getMessage());
            return "admin/users";
        }
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("pageTitle", "Courses");
        model.addAttribute("courses", courseRepository.findAll());
        return "admin/courses";
    }

    @PostMapping("/courses")
    public String createCourse(@RequestParam String code,
                               @RequestParam String title,
                               @RequestParam int credits) {
        Course c = new Course();
        c.setCode(code);
        c.setTitle(title);
        c.setCredits(credits);
        courseRepository.save(c);
        return "redirect:/admin/courses";
    }

    @GetMapping("/offerings")
    public String offerings(Model model) {
        model.addAttribute("pageTitle", "Course Offerings");
        model.addAttribute("offerings", offeringRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("facultyList", getFacultyUsers());
        return "admin/offerings";
    }

    @PostMapping("/offerings")
    public String createOffering(@RequestParam Long courseId,
                                 @RequestParam Long facultyId,
                                 @RequestParam String section) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        User faculty = getFacultyUsers().stream()
                .filter(u -> u.getId().equals(facultyId))
                .findFirst()
                .orElseThrow();
        CourseOffering offering = new CourseOffering();
        offering.setCourse(course);
        offering.setFaculty(faculty);
        offering.setSection(section);
        offeringRepository.save(offering);
        return "redirect:/admin/offerings";
    }

    @PostMapping("/offerings/{id}/delete")
    public String deleteOffering(@PathVariable Long id) {
        offeringRepository.deleteById(id);
        return "redirect:/admin/offerings";
    }

    private List<User> getFacultyUsers() {
        return userService.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleName.ROLE_FACULTY))
                .toList();
    }
}

