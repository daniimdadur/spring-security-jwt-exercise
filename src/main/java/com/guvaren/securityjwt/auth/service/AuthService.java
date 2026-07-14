package com.guvaren.securityjwt.auth.service;

import com.guvaren.securityjwt.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.auth.dto.res.AuthenticationRes;
import com.guvaren.securityjwt.auth.dto.res.TokenRes;

public interface AuthService {
    AuthenticationRes register(RegistrationReq req);
    AuthenticationRes login(AuthenticationReq req);
    TokenRes getNewAccessToken(String refreshToken);
    String Logout(String refreshToken);
}
