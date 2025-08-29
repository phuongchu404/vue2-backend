package vn.mk.eid.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * bean
 *
 * @author mk.com.vn
 * @date 2017/10/17
 */
@Component("applicationContextProvider")
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }

}
