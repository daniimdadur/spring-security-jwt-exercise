package com.guvaren.securityjwt.util;

import java.util.UUID;

public class CommonUtil {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
