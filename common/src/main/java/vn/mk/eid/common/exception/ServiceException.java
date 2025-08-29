package vn.mk.eid.common.exception;

import lombok.Getter;
import vn.mk.eid.common.data.ResultCode;

/**
 * Service exception
 */
public class ServiceException extends RuntimeException {

    @Getter
    private final String resultCode;

    public ServiceException(String resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ServiceException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getDescription());
    }
}
