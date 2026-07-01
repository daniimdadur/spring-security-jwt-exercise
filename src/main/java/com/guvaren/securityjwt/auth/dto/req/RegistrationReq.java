package com.guvaren.securityjwt.auth.dto.req;

import com.guvaren.securityjwt.auth.enums.Roles;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationReq {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<Roles> roles;
}
