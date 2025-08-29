package vn.mk.eid.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.exception.ServiceException;

/**
 * @author mk.com.vn
 * @date 2018/7/16 11:22
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceExceptionHandler {
    @Pointcut("execution(vn.mk.eid.common.data.ServiceResult vn.mk.eid.visitor..service..*ServiceImpl..*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        log.debug("aspect is called.");
        try {
            return joinPoint.proceed();
        } catch (ServiceException e) {
            printLog(joinPoint);
            log.error("Service exception occured: {}", e);
            return ServiceResult.fail(e.getResultCode(), e.getMessage());
        } catch (Exception e) {
            printLog(joinPoint);
            log.error("Unknow exception occured:", e);
            return ServiceResult.fail(ResultCode.UNKNOWN_ERROR.getCode(), ResultCode.UNKNOWN_ERROR.getDescription());
        } catch (Throwable e) {
            printLog(joinPoint);
            log.error("Unknown throwable occured: {}", e);
            return ServiceResult.fail(ResultCode.UNKNOWN_ERROR.getCode(), ResultCode.UNKNOWN_ERROR.getDescription());
        }
    }

    private void printLog(ProceedingJoinPoint joinPoint) {
        String clazz = joinPoint.getSignature().getDeclaringTypeName();
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.error("failed to invoke [{}.{}], args value are: {}", clazz, method, args);
    }

}
