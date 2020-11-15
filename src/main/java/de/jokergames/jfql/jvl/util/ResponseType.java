package de.jokergames.jfql.jvl.util;

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

        for (ResponseType type : ResponseType.values()) {
            if (type.rCode == rCode) {
                return type;
            }
        }

        return null;
    }

    public int getRCode() {
        return rCode;
    }

}