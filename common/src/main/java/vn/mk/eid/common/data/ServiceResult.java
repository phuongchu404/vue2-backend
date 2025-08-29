package vn.mk.eid.common.data;

import lombok.extern.slf4j.Slf4j;
import vn.mk.eid.common.exception.ServiceException;

import java.io.Serializable;

@Slf4j
public class ServiceResult<T> implements Serializable {
    private static final long serialVersionUID = 1428484501441111860L;
    private boolean success;
    private String code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return "0".equals(code);
//        return this.success;
    }


    public boolean getSuccess() {
//        return "0".equals(code);
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(String resultCode, String message) {
        this.success = false;
        this.code = resultCode;
        this.message = message;
    }

    public static <T> ServiceResult<T> ok(T data) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setData(data);
        result.setMessage(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getDescription());
        result.setSuccess(true);
        return result;
    }

    public static <T> ServiceResult<T> ok() {
        return ServiceResult.ok(null);
    }

    public static <T> ServiceResult<T> fail(String resultCode, String error) {
        //log.debug(error);
        ServiceResult<T> resp = new ServiceResult<>();
        resp.setMessage(resultCode, error);
        return resp;
    }

    public static <T> ServiceResult<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getDescription());
    }

    public static <T> ServiceResult<T> fail(ResultCode resultCode, Exception ex) {
        return fail(resultCode.getCode(), resultCode.getDescription() + ": " + ex.getMessage());
    }

    public static <T> ServiceResult<T> fail(ServiceException e) {
        return fail(e.getResultCode(), e.getMessage());
    }

    public T successData() {
        if (!isSuccess()) {
            throw new ServiceException(getCode(), getMessage());
        }
        return data;
    }


    public static <T> ServiceResult<T> fail(T data, String resultCode, String error) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setData(data);
        result.setMessage(resultCode, error);
        return result;
    }

    public static <T> ServiceResult<T> fail(T data, ResultCode resultCode) {
        ServiceResult<T> result = new ServiceResult<>();

        result.setMessage(resultCode.getCode(), resultCode.getDescription());
        result.setData(data);
        return result;
    }

}
