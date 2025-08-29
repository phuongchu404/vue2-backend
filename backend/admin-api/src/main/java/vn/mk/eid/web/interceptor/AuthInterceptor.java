package vn.mk.eid.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import vn.mk.eid.common.exception.ServiceException;
import vn.mk.eid.common.response.RoleVO;
import vn.mk.eid.common.response.UserVO;
import vn.mk.eid.user.cache.PermissionCache;
import vn.mk.eid.user.service.TokenService;
import vn.mk.eid.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static vn.mk.eid.common.data.ResultCode.NO_ACCESS_RIGHT;


@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final UserService userService;
    private final TokenService tokenService;
    private final PermissionCache permissionCache;


    public AuthInterceptor(UserService userService, TokenService tokenService, PermissionCache permissionCache) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.permissionCache = permissionCache;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
        if (writeList(requestUrl)) {
            return true;
        }
        String token = request.getHeader("x-access-token");
        if (StringUtils.isEmpty(token)) {
            throw new ServiceException(NO_ACCESS_RIGHT);
        }
        UserVO userVO = tokenService.loadToken(token).successData();
        userService.verifyToken(userVO.getUserName(), token).successData();
        tokenService.refreshToken(token).successData();
        if (userVO.getId() == 1) {
            return true;
        }
        List<Integer> roleIds = userVO.getRoles().stream().map(RoleVO::getId).collect(Collectors.toList());
        checkPermission(request.getMethod(), requestUrl, roleIds);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    private final String[] whiteList = {
            "/**/*.html",
            "/**/*.js",
            "/**/*.css",
            "/**/*.png",
            "/**/*.gif",
            "/**/*.jp?g",
            "/**/*.jpg",
            "/**/*.woff?",
            "/**/*.js.map",
            "/**/*.ttf",
            "/",
            "/error",
            "/api/sessions/login",
            "/api/sessions/secret/image/{uuid}",
            "/swagger*/**",
            "/validatorUrl",
            "/api/mobile/**",
            "/api/sessions/permission/**",
            "/api/public/**",
            "/api/device/count",
            "/api/reader/count",
            "/api/device/update/**",
//            "/api/admin/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"

    };

    private boolean writeList(String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        for (String aWhiteList : whiteList) {
            if (matcher.match(aWhiteList, url)) {
                return true;
            }
        }
        return false;
    }

    private void checkPermission(String method, String url, List<Integer> roleIds) {
        if (!permissionCache.match(method, url, roleIds)) {
            throw new ServiceException(NO_ACCESS_RIGHT);
        }
    }
}
