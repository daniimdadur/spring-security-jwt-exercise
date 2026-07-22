package com.guvaren.securityjwt.master.auth.controller;

import com.guvaren.securityjwt.base.Response;
import com.guvaren.securityjwt.master.auth.dto.req.RolesReq;
import com.guvaren.securityjwt.master.auth.dto.res.UserRes;
import com.guvaren.securityjwt.master.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiUserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response<Object>> get() {
        List<UserRes> result = this.userService.get();
        return ResponseEntity.ok(Response.success(result));
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Response<String>> updateNewRoles(@RequestBody RolesReq req, @PathVariable String userId) {
        String message = this.userService.updateNewRoles(req, userId);
        return ResponseEntity.ok(Response.updated(message));
    }
}
