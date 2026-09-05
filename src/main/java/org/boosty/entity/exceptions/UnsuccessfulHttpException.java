package org.boosty.entity.exceptions;

import lombok.Getter;

@Getter
public class UnsuccessfulHttpException extends Exception {

    private final int code;
    private final String responseBody;

    public UnsuccessfulHttpException(int code, String responseBody) {
        super("The server responded with code: " + code + ", message: " + responseBody);
        this.code = code;
        this.responseBody = responseBody;
    }
}