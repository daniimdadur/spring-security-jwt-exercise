package com.guvaren.securityjwt.master.auth.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolesTest {

    @Test
    void values_shouldHaveThreeEntries() {
        assertEquals(3, Roles.values().length);
    }

    @Test
    void shouldContainUser() {
        assertNotNull(Roles.valueOf("USER"));
    }

    @Test
    void shouldContainAdmin() {
        assertNotNull(Roles.valueOf("ADMIN"));
    }

    @Test
    void shouldContainSuperAdmin() {
        assertNotNull(Roles.valueOf("SUPER_ADMIN"));
    }
}
