package net.junhabaek.authserver.common.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class MdcInterceptor implements HandlerInterceptor {
    public static final String HEADER_REQUEST_UUID_KEY = "x-request-id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestEventId = request.getHeader(HEADER_REQUEST_UUID_KEY);

        if (!StringUtils.hasText(requestEventId)) {
            requestEventId = UUID.randomUUID().toString();
        }

        MDC.put(HEADER_REQUEST_UUID_KEY, requestEventId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        MDC.clear();
    }
}
