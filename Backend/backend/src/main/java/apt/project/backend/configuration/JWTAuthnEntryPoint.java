package apt.project.backend.configuration;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Spring Security uses a series of filters to manage security. When a request comes in that requires authentication but 
// no user is logged in, Spring Security needs to decide what to do. This decision-making process is the job of the AuthenticationEntryPoint. 
// It intercepts these unauthenticated requests and can direct them to a login page, return an HTTP status code like 401 (Unauthorized), 
// or perform any custom logic you define.

@Component
@Configuration
public class JWTAuthnEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException)
            throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{ \"message\" : \"Please login to proceed with this action\" }");
    }
}
