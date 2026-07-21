package com.guvaren.securityjwt.master.auth.service;

import com.guvaren.securityjwt.exception.BadRequestException;
import com.guvaren.securityjwt.exception.NotFoundException;
import com.guvaren.securityjwt.master.auth.dto.req.RolesReq;
import com.guvaren.securityjwt.master.auth.dto.res.UserRes;
import com.guvaren.securityjwt.master.auth.entity.RoleEntity;
import com.guvaren.securityjwt.master.auth.entity.UserEntity;
import com.guvaren.securityjwt.master.auth.repository.RoleRepo;
import com.guvaren.securityjwt.master.auth.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Override
    public List<UserRes> get() {
        List<UserEntity> users = this.userRepo.findAll();
        return users.stream().map(user -> UserRes.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(RoleEntity::getRole).collect(Collectors.toSet()))
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updateNewRoles(RolesReq req, String id) {
        UserEntity user = this.userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            Set<RoleEntity> roles = this.roleRepo.findByRoleIn(req.getRoles());

            if (roles.size() != req.getRoles().size()) {
                throw new BadRequestException("One or more specified roles do not exist");
            }

            user.getRoles().addAll(roles);
        }
        return "User roles updated successfully";
    }
}
