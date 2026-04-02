package curse.auth.jwt.security;


import curse.auth.exceptions.CustomJwtAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String errorMessage = (authException instanceof CustomJwtAuthenticationException)
                ? authException.getMessage()
                : "Full authentication is required to access this resource";

        response.getWriter().write(String.format("{ \"error\": \"unauthorized\", \"message\": \"%s\" }", errorMessage));
    }
}
