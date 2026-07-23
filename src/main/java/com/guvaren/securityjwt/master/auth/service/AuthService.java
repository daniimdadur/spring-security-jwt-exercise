package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.master.auth.dto.req.AuthenticationReq;
import com.guvaren.securityjwt.master.auth.dto.req.RegistrationReq;
import com.guvaren.securityjwt.master.auth.dto.res.AuthenticationRes;
import com.guvaren.securityjwt.master.auth.dto.res.TokenRes;

public interface AuthService {
    AuthenticationRes register(RegistrationReq req);
    AuthenticationRes login(AuthenticationReq req);
    TokenRes getNewAccessToken(String refreshToken);
    void logout(String refreshToken);
}
