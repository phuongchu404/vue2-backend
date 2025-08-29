package vn.mk.eid.web.utils;


import org.springframework.util.StringUtils;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;

public class ParametersValidator {
    List<ResultCode> results;

    public ParametersValidator() {
        results = new ArrayList<ResultCode>();
    }

    public static ParametersValidator getInstance() {
        return new ParametersValidator();
    }

    public ParametersValidator addUsername(String userName) {
        if (StringUtils.isEmpty(userName)) {
            results.add(ResultCode.INVALID_USER_NAME);
        }
        validate();
        return this;
    }

    public ParametersValidator addIsEmpty(Object param, String paramName) {
        if (StringUtils.isEmpty(param)) {
            ResultCode.VALIDATE_ERROR.setDescription(paramName + "Không được để trống hoặc độ dài không được bằng 0");
            results.add(ResultCode.VALIDATE_ERROR);
        }
        validate();
        return this;
    }

    public ParametersValidator addIsEmpty(String param, String paramName) {
        if (StringUtils.isEmpty(param)) {
            ResultCode.VALIDATE_ERROR.setDescription(paramName + "Không được để trống hoặc độ dài không được bằng 0");
            results.add(ResultCode.VALIDATE_ERROR);
        }
        validate();
        return this;
    }

    public ParametersValidator addListEmpty(List<?> param, String paramName) {
        if (StringUtils.isEmpty(param)) {
            ResultCode.VALIDATE_ERROR.setDescription(paramName + "Không được để trống hoặc độ dài không được bằng 0");
            results.add(ResultCode.VALIDATE_ERROR);
        }
        validate();
        return this;
    }

    public ParametersValidator addLength(String param, int maxLength, String paramName) {
        if (param != null && param.length() > maxLength) {
            ResultCode.VALIDATE_ERROR.setDescription(paramName + "chiều dài không thể vượt quá" + maxLength);
            results.add(ResultCode.VALIDATE_ERROR);
        }
        validate();
        return this;
    }

    public ParametersValidator addEqualsLength(String param,int len,String paramName){
        if (param != null && param.length() != len) {
            ResultCode.VALIDATE_ERROR.setDescription(paramName + "chiều dài phải là" + len);
            results.add(ResultCode.VALIDATE_ERROR);
        }
        validate();
        return this;
    }

    public void validate() {
        if (!results.isEmpty()) {
            throw new ServiceException(results.get(0));
        }
    }
}
