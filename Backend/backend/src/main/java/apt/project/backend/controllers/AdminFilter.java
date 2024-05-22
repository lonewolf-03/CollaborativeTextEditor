package apt.project.backend.controllers;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import apt.project.backend.services.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class AdminFilter extends GenericFilterBean{
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;


    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("Entered the filter");
        HttpServletRequest req = (HttpServletRequest) request;
        if(req.getRequestURL().toString().contains("/admin")){
            String auth[] = req.getHeader("Authorization").split(",");
            if(auth.length == 2){
                String email = auth[0];
                String password = auth[1];
                try{
                    Optional<Long> Id = userRepository.getIdFromEmail(email);
                    if(Id.isPresent()){
                        String role = userRepository.getUserRole(Id.get()).get();
                        if(role.equals("ADMIN")){
                            System.out.println("ADMIN AUTHENTICATED");
                            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }catch(Exception e){
                    chain.doFilter(request, response);
                }
            }
        }
        chain.doFilter(request, response);
    }
    
    
}
