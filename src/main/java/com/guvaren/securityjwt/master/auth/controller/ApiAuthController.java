package com.guvaren.securityjwt.master.auth.controller;

import com.guvaren.securityjwt.master.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.master.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.master.auth.dto.res.AuthenticationRes;
import com.guvaren.securityjwt.master.auth.dto.res.TokenRes;
import com.guvaren.securityjwt.master.auth.service.AuthService;
import com.guvaren.securityjwt.base.Response;
import com.guvaren.securityjwt.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ApiAuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<Response<AuthenticationRes>> register(@RequestBody RegistrationReq req, HttpServletResponse response) {
        AuthenticationRes res = this.authService.register(req);
        this.cookieUtil.addRefreshTokenCookie(response, res.getRefreshToken());
        return ResponseEntity.ok(Response.created(res));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<AuthenticationRes>> login(@RequestBody AuthenticationReq req, HttpServletResponse response) {
        AuthenticationRes res = this.authService.login(req);
        this.cookieUtil.addRefreshTokenCookie(response, res.getRefreshToken());
        return ResponseEntity.ok(Response.success(res));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Response<TokenRes>> refreshToken(@CookieValue(name = "refresh_token") String refreshToken) {
        TokenRes res = this.authService.getNewAccessToken(refreshToken);
        return ResponseEntity.ok(Response.created(res));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(@CookieValue(name = "refresh_token") String refreshToken) {
        this.authService.logout(refreshToken);
        return ResponseEntity.ok(Response.success("Logout successful"));
    }
}
