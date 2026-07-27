package com.guvaren.securityjwt.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomStatusTest {

    @Test
    void success_shouldHaveCode200() {
        assertEquals(200, CustomStatus.SUCCESS.getCode());
    }

    @Test
    void success_shouldHaveMessage() {
        assertEquals("Success", CustomStatus.SUCCESS.getMessage());
    }

    @Test
    void updated_shouldHaveCode200() {
        assertEquals(200, CustomStatus.UPDATED.getCode());
    }

    @Test
    void updated_shouldHaveMessage() {
        assertEquals("Updated", CustomStatus.UPDATED.getMessage());
    }

    @Test
    void deleted_shouldHaveCode200() {
        assertEquals(200, CustomStatus.DELETED.getCode());
    }

    @Test
    void deleted_shouldHaveMessage() {
        assertEquals("Deleted", CustomStatus.DELETED.getMessage());
    }

    @Test
    void values_shouldHaveThreeEntries() {
        assertEquals(3, CustomStatus.values().length);
    }
}
