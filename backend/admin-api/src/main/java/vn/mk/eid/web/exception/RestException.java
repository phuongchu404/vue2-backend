package vn.mk.eid.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * rest exception
 *
 * @author liukeshao
 * @date 2017/10/16
 */
public class RestException extends RuntimeException {

    private static final long serialVersionUID = -1951166201567346124L;

    @Getter
    private final HttpStatus status;

    public RestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
