package com.rica.publicaciones.observabilidad;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.MDC;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class CorrelacionIdInterceptor implements HandlerInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String MDC_CORRELATION_ID = "correlationId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String id = request.getHeader(CORRELATION_ID_HEADER);
        
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
        
        MDC.put(MDC_CORRELATION_ID, id);
        response.setHeader(CORRELATION_ID_HEADER, id);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(MDC_CORRELATION_ID);
    }

    public static String getCorrelationId() {
        return MDC.get(MDC_CORRELATION_ID);
    }
}
