package com.guvaren.securityjwt.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonUtilTest {

    @Test
    void getUUID_shouldReturnNonEmptyString() {
        String uuid = CommonUtil.getUUID();
        assertNotNull(uuid);
        assertFalse(uuid.isEmpty());
    }

    @Test
    void getUUID_shouldNotContainDashes() {
        String uuid = CommonUtil.getUUID();
        assertFalse(uuid.contains("-"), "UUID should not contain dashes");
    }

    @Test
    void getUUID_shouldHave32Characters() {
        String uuid = CommonUtil.getUUID();
        assertEquals(32, uuid.length(), "UUID without dashes should be 32 characters");
    }

    @Test
    void getUUID_shouldBeValidHex() {
        String uuid = CommonUtil.getUUID();
        assertTrue(uuid.matches("[0-9a-f]+"), "UUID should contain only hexadecimal characters");
    }

    @Test
    void getUUID_shouldGenerateUniqueValues() {
        String uuid1 = CommonUtil.getUUID();
        String uuid2 = CommonUtil.getUUID();
        assertNotEquals(uuid1, uuid2, "Each call should generate a unique UUID");
    }
}
