package vn.mk.eid.user.service;

import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.response.UserVO;

/**
 * @author mk
 * @since 2024-07-26
 */
public interface TokenService {
    String PREFIX_TOKEN = "MK_TOKEN_";

    ServiceResult<String> generateToken(String username, String secret);

    ServiceResult<UserVO> loadToken(String token);

    ServiceResult<Boolean> storeToken(String token, UserVO userVO);

    ServiceResult<Boolean> refreshToken(String token);

    ServiceResult<Boolean> clearToken(String userName, String secret);

}
