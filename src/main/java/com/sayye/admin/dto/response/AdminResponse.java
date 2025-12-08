package com.sayye.admin.dto.response;

import com.sayye.admin.Role;
import com.sayye.admin.entity.Admin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class AdminResponse {

    private final Long id;

    private final String userId;

    private final String name;

    private final String email;

    private final Role role;

    public AdminResponse(Admin admin) {
        this.id = admin.getId();
        this.userId = admin.getUserId();
        this.name = admin.getName();
        this.email = admin.getEmail();
        this.role = admin.getRole();
    }

}
