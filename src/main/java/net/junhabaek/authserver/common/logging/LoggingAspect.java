package net.junhabaek.authserver.common.logging;

import lombok.RequiredArgsConstructor;
import net.junhabaek.authserver.common.interceptor.MdcInterceptor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private static final Marker EXECUTION_MARKER = MarkerFactory.getMarker("EXECUTION");
    private static final Marker EXCEPTION_MARKER = MarkerFactory.getMarker("EXCEPTION");
    private final Environment env;

    /*
        Repository, Service, RestController Annotation이 달린 대상일 것.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void coreComponentPointcut() {
    }

    /*
        아래와 같은 패키지 내에 있을 것.
     */
    @Pointcut("within(net.junhabaek.authserver.repository..*)"+
            " || within(net.junhabaek.authserver.service..*)"+
            " || within(net.junhabaek.authserver.web.rest..*)")
    public void packageBasedPointcut() {
    }

    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    @Around("packageBasedPointcut() || coreComponentPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = logger(joinPoint);

        // 성능 개선 위해 log.level이 debug일 때만 출력
        if (log.isDebugEnabled()) {
            // 메소드 실행 기록
            log.debug(EXECUTION_MARKER, "Request [{}] : Method execute {}() with arguments = {}",
                    getRequestId(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();

            if (log.isDebugEnabled()) {
                // 메서드 수행 결과
                log.debug(EXECUTION_MARKER, "Request [{}] : Method complete {}() with result = {}",
                        getRequestId(),
                        joinPoint.getSignature().getName(),
                        result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            // Argument exception이 발생했다면, 해당 argument들을 error 로그에 작성한다.
            log.error(EXCEPTION_MARKER, "Request [{}] Illegal argument: {} in {}()",
                    getRequestId(),
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }

    private String getRequestId() {
        return MDC.get(MdcInterceptor.HEADER_REQUEST_UUID_KEY);
    }

    /*
        Exception이 발생했을 때, 이에 대한 로그를 작성한다.
     */
    @AfterThrowing(pointcut = "coreComponentPointcut() && packageBasedPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Logger log = logger(joinPoint);

        if (env.acceptsProfiles(Profiles.of("dev"))) {
            log
                .error(
                        EXCEPTION_MARKER,
                        "Request [{}] : Exception Occurred in {}() with cause = '{}' and exception name = '{}'",
                        getRequestId(),
                        joinPoint.getSignature().getName(),
                        e.getCause() != null ? e.getCause() : "null",
                        e.getMessage(),
                        e
                );
        } else {
            log
                .error(
                        EXCEPTION_MARKER,
                        getRequestId(),
                        "Exception Occurred in {}() with cause = {}",
                        joinPoint.getSignature().getName(),
                        e.getCause() != null ? e.getCause() : "null"
                );
        }
    }
}
