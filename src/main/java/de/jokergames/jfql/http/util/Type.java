package de.jokergames.jfql.http.util;

/**
 * @author Janick
 */

public enum Type {

    SUCCESS(200),
    REST(200),
    FORBIDDEN(403),
    NOT_FOUND(404),
    SYNTAX_ERROR(501),
    BAD_METHOD(500);

    int rCode;

    Type(int rCode) {
        this.rCode = rCode;
    }

    public static Type byRCode(int rCode) {

        for (Type type : Type.values()) {
            if (type.rCode == rCode) {
                return type;
            }
        }

        return null;
    }

    public int getrCode() {
        return rCode;
    }

}
