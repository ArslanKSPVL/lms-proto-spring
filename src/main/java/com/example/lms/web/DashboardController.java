package com.example.lms.web;

import com.example.lms.security.RoleName;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.contains(RoleName.ROLE_STUDENT.name())) {
            return "redirect:/student/dashboard";
        } else if (roles.contains(RoleName.ROLE_FACULTY.name())
                || roles.contains(RoleName.ROLE_HOD.name())
                || roles.contains(RoleName.ROLE_DEAN.name())) {
            return "redirect:/faculty/dashboard";
        } else if (roles.contains(RoleName.ROLE_EXAM_CELL.name())) {
            return "redirect:/exam/dashboard";
        } else if (roles.contains(RoleName.ROLE_IQAC.name())) {
            return "redirect:/iqac/dashboard";
        } else if (roles.contains(RoleName.ROLE_ADMIN.name()) || roles.contains(RoleName.ROLE_SUPER_ADMIN.name())) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/login";
    }
}

