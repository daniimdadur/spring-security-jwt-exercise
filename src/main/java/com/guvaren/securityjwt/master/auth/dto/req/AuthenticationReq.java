package com.guvaren.securityjwt.master.auth.dto.req;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationReq {
    private String email;
    private String password;
}
