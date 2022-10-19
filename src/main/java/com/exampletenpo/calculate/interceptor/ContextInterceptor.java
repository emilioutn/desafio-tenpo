package com.exampletenpo.calculate.interceptor;

import com.exampletenpo.calculate.config.context.ContextHolder;
import com.exampletenpo.calculate.dto.history.HistoryDto;
import com.exampletenpo.calculate.service.HistoryService;
import com.exampletenpo.calculate.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class ContextInterceptor implements HandlerInterceptor {

    private final SecurityService securityService;
    private final HistoryService historyService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ContextHolder.setIpFrom(getClientIpAddress(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        createCallHistory(request, response);
        ContextHolder.clear();
    }

    private void createCallHistory(HttpServletRequest request, HttpServletResponse response) {
        HistoryDto historyDto = null;
        if (!request.getRequestURL().toString().contains("call-history")) {
            historyDto = HistoryDto
                    .builder()
                    .method(request.getMethod())
                    .path(request.getRequestURL().toString())
                    .username(securityService.getThreadAccountUserName())
                    .ipFrom(getClientIpAddress(request))
                    .status(response.getStatus())
                    .result(ContextHolder.getContext().getResponse())
                    .build();
            historyService.createHistory(historyDto);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    private final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };
}
