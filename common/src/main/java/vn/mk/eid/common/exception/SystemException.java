package vn.mk.eid.common.exception;

/**
 * @author mk.com.vn
 * @date 2018/12/28 16:40
 */
public class SystemException extends RuntimeException {
    public SystemException() {
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
