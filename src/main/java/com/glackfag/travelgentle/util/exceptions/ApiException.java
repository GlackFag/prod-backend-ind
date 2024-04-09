package com.glackfag.travelgentle.util.exceptions;

/**
 * Выкидывается, если используемые API недоступны или вернули некорректные данные
 */
public class ApiException extends RuntimeException {
    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }
}
