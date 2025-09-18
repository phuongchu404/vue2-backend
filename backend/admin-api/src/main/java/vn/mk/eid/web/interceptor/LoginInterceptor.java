package vn.mk.eid.web.interceptor;


import vn.mk.eid.common.response.UserVO;
import vn.mk.eid.user.cache.PermissionCache;
import vn.mk.eid.user.service.TokenService;
import vn.mk.eid.user.utils.CurrentUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private final TokenService tokenService;
    private final PermissionCache permissionCache;

    public LoginInterceptor(TokenService tokenService, PermissionCache permissionCache) {
        this.tokenService = tokenService;
        this.permissionCache = permissionCache;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("x-access-token");
        if (token != null) {
            UserVO userVO = tokenService.loadToken(token).successData();
            CurrentUser.setUser(userVO);
            permissionCache.reinitPermissionCache();
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        CurrentUser.clear();
    }
}
