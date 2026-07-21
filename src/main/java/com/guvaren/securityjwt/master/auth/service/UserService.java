package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.master.auth.dto.req.RolesReq;
import com.guvaren.securityjwt.master.auth.dto.res.UserRes;

import java.util.List;

public interface UserService {
    List<UserRes> get();
    String updateNewRoles(RolesReq req, String id);
}
