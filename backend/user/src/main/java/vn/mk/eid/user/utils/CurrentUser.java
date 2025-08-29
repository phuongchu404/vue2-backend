package vn.mk.eid.user.utils;

import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.exception.ServiceException;
import vn.mk.eid.common.response.UserVO;

/**
 * @author mk
 * @since 2024-07-26
 */
public class CurrentUser {
    private static final ThreadLocal<UserVO> userVOThreadLocal = new ThreadLocal<>();

    public static void setUser(UserVO user) {
        userVOThreadLocal.set(user);
    }

    public static void clear() {
        userVOThreadLocal.remove();
    }

    public static UserVO getLoginUser() {
        UserVO userVO = userVOThreadLocal.get();
        if (userVO == null) {
            throw new ServiceException(ResultCode.EXPIRED_TOKEN);
        }
        return userVO;
    }
}
