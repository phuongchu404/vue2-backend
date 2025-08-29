package vn.mk.eid.web.interceptor;


import vn.mk.eid.common.response.UserVO;
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

    public LoginInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("x-access-token");
        if (token != null) {
            UserVO userVO = tokenService.loadToken(token).successData();
            CurrentUser.setUser(userVO);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        CurrentUser.clear();
    }
}
