package vn.mk.eid.common.annotation;

import org.springframework.context.annotation.Import;
import vn.mk.eid.common.handler.ServiceExceptionHandler;

import java.lang.annotation.*;

/**
 * @author mk.com.vn
 * @date 2018/7/16 11:33
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(ServiceExceptionHandler.class)
public @interface EnableServiceExceptionHandler {

}
