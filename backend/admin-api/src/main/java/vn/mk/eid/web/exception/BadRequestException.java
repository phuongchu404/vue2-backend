package vn.mk.eid.web.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
