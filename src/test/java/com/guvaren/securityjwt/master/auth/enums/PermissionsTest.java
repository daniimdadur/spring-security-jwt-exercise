package com.guvaren.securityjwt.master.auth.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionsTest {

    @Test
    void userRead_shouldHaveCorrectValue() {
        assertEquals("user:read", Permissions.USER_READ.getValue());
    }

    @Test
    void userCreate_shouldHaveCorrectValue() {
        assertEquals("user:create", Permissions.USER_CREATE.getValue());
    }

    @Test
    void userUpdate_shouldHaveCorrectValue() {
        assertEquals("user:update", Permissions.USER_UPDATE.getValue());
    }

    @Test
    void userDelete_shouldHaveCorrectValue() {
        assertEquals("user:delete", Permissions.USER_DELETE.getValue());
    }

    @Test
    void userAssignRole_shouldHaveCorrectValue() {
        assertEquals("user:assign-role", Permissions.USER_ASSIGN_ROLE.getValue());
    }

    @Test
    void roleRead_shouldHaveCorrectValue() {
        assertEquals("role:read", Permissions.ROLE_READ.getValue());
    }

    @Test
    void roleCreate_shouldHaveCorrectValue() {
        assertEquals("role:create", Permissions.ROLE_CREATE.getValue());
    }

    @Test
    void roleUpdate_shouldHaveCorrectValue() {
        assertEquals("role:update", Permissions.ROLE_UPDATE.getValue());
    }

    @Test
    void roleDelete_shouldHaveCorrectValue() {
        assertEquals("role:delete", Permissions.ROLE_DELETE.getValue());
    }

    @Test
    void fakultasRead_shouldHaveCorrectValue() {
        assertEquals("fakultas:read", Permissions.FAKULTAS_READ.getValue());
    }

    @Test
    void fakultasCreate_shouldHaveCorrectValue() {
        assertEquals("fakultas:create", Permissions.FAKULTAS_CREATE.getValue());
    }

    @Test
    void fakultasUpdate_shouldHaveCorrectValue() {
        assertEquals("fakultas:update", Permissions.FAKULTAS_UPDATE.getValue());
    }

    @Test
    void fakultasDelete_shouldHaveCorrectValue() {
        assertEquals("fakultas:delete", Permissions.FAKULTAS_DELETE.getValue());
    }

    @Test
    void values_shouldHave13Entries() {
        assertEquals(13, Permissions.values().length);
    }
}
