package com.guvaren.securityjwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${app.cookie.secure:false}")
    private boolean secure;

    @Value("${app.cookie.max-age:1440}")
    private int maxAge;

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(this.secure);
        cookie.setPath("/");
        cookie.setMaxAge(this.maxAge);
        //cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }
}
