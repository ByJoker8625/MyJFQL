package de.byjoker.myjfql.exception;

public class TableException extends RuntimeException {

    public TableException() {
    }

    public TableException(String message) {
        super(message);
    }

    public TableException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableException(Throwable cause) {
        super(cause);
    }

    public TableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
