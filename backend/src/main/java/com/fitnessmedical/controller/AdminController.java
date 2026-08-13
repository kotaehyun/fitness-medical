package com.fitnessmedical.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.dto.admin.AdminMemberResponse;
import com.fitnessmedical.service.AdminService;

/**
 * [공부/면접] 관리자 API — 가입 현황 + 전문직 인증
 *
 * Q. URL이 /api/admin 인 이유?
 * A. SecurityConfig에서 hasRole("ADMIN")으로 이 경로만 막는다.
 *    회원/전문가 API와 권한을 섞지 않는다.
 *
 * Q. licenseNumber를 JSON에 넣나?
 * A. 넣지 않는다. AccountResponse는 인증 여부만 내려준다.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/members")
    public List<AdminMemberResponse> listMembers(
            @AuthenticationPrincipal UserDetails user
    ) {
        return adminService.listMembers(loginId(user));
    }

    @GetMapping("/professionals")
    public List<AccountResponse> listProfessionals(
            @AuthenticationPrincipal UserDetails user
    ) {
        return adminService.listProfessionals(loginId(user));
    }

    @PostMapping("/professionals/{accountId}/verify")
    public AccountResponse verifyProfessional(
            @PathVariable Long accountId,
            @AuthenticationPrincipal UserDetails user
    ) {
        return adminService.setProfessionalVerified(loginId(user), accountId, true);
    }

    @PostMapping("/professionals/{accountId}/revoke")
    public AccountResponse revokeProfessional(
            @PathVariable Long accountId,
            @AuthenticationPrincipal UserDetails user
    ) {
        return adminService.setProfessionalVerified(loginId(user), accountId, false);
    }

    private String loginId(UserDetails user) {
        return user == null ? null : user.getUsername();
    }
}
