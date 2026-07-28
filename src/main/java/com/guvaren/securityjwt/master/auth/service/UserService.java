package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.master.auth.dto.req.RolesReq;
import com.guvaren.securityjwt.master.auth.dto.res.UserRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserRes> get();
    Page<UserRes> get(Pageable pageable);
    String updateNewRoles(RolesReq req, String id);
}
