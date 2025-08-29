package vn.mk.eid.web;


import vn.mk.eid.web.interceptor.AuthInterceptor;
import vn.mk.eid.web.interceptor.LoginInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ks
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final ApplicationContext applicationContext;

    public WebConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(applicationContext.getBean(LoginInterceptor.class));
        registry.addInterceptor(applicationContext.getBean(AuthInterceptor.class));
    }
}
