package io.kontur.layers.controller.exceptions;

import org.springframework.http.HttpStatus;

public class WebApplicationException extends RuntimeException {

    private HttpStatus status;
    private Error err;

    public WebApplicationException(final HttpStatus status, final Error err, final Throwable cause) {
        super(err.getMsg(), cause);
        this.status = status;
        this.err = err;
    }

    public WebApplicationException(final HttpStatus status, final String err) {
        this(status, Error.error(err));
    }

    public WebApplicationException(final HttpStatus status, final Error err) {
        this(status, err, null);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Error getErr() {
        return err;
    }
}
