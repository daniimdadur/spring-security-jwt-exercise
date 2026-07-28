package com.guvaren.securityjwt.master.auth.controller;

import com.guvaren.securityjwt.base.PageResponse;
import com.guvaren.securityjwt.base.Response;
import com.guvaren.securityjwt.master.auth.dto.req.RolesReq;
import com.guvaren.securityjwt.master.auth.dto.res.UserRes;
import com.guvaren.securityjwt.master.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiUserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response<PageResponse<UserRes>>> get(
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {
        Page<UserRes> result = this.userService.get(pageable);
        return ResponseEntity.ok(Response.successPage(result));
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Response<String>> updateNewRoles(@RequestBody RolesReq req, @PathVariable String userId) {
        String message = this.userService.updateNewRoles(req, userId);
        return ResponseEntity.ok(Response.updated(message));
    }
}
