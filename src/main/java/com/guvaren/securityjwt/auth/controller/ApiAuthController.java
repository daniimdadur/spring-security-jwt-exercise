package com.guvaren.securityjwt.auth.controller;

import com.guvaren.securityjwt.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.auth.dto.res.AuthenticationRes;
import com.guvaren.securityjwt.auth.service.AuthService;
import com.guvaren.securityjwt.base.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ApiAuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<AuthenticationRes>> register(@RequestBody RegistrationReq req) {
        AuthenticationRes res = this.authService.register(req);
        return ResponseEntity.ok(Response.created(res));
    }
}
