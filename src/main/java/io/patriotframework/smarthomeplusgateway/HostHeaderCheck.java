package io.patriotframework.smarthomeplusgateway;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

/**
 * This class is responsible for checking if the host header is allowed
 */
@Component
public class HostHeaderCheck extends OncePerRequestFilter {

    @Value("${whitelist.allowedHosts}")
    private List<String> ALLOWED_HOSTS;

    @Value("${whitelist.turnOn}")
    private boolean TURN_ON;

    /**
     * This method is responsible for checking if the host header is allowed
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        System.out.println("HostHeaderCheck");
        String host = request.getHeader("Host");
        System.out.println(ALLOWED_HOSTS);
        System.out.println(host);
        if (!TURN_ON) {
            filterChain.doFilter(request, response);
            return;
        } else {
            for (String allowedHost : ALLOWED_HOSTS) {
                if (host.equals(allowedHost)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
