package org.jokergames.myjfql.server.util;

import java.util.Arrays;

/**
 * @author Janick
 */

public enum ResponseType {

    SUCCESS(200),
    REST(200),
    FORBIDDEN(403),
    NOT_FOUND(404),
    SYNTAX_ERROR(501),
    BAD_METHOD(500);

    int rCode;

    ResponseType(int rCode) {
        this.rCode = rCode;
    }

    public static ResponseType byRCode(int rCode) {
        return Arrays.stream(ResponseType.values()).filter(type -> type.rCode == rCode).findFirst().orElse(null);
    }

    public int getRCode() {
        return rCode;
    }

}