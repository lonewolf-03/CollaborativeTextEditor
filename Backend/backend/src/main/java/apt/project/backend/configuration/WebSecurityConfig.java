package apt.project.backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import apt.project.backend.controllers.AdminFilter;
import apt.project.backend.controllers.JWTAuthnFilter;
import apt.project.backend.services.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{
    private UserService userService;
    private JWTAuthnEntryPoint authnEntryPoint;

    @Autowired
    WebSecurityConfig(UserService userService, JWTAuthnEntryPoint authnEntryPoint){
        this.userService = userService;
        this.authnEntryPoint = authnEntryPoint;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authnEntryPoint))
        .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/login/**")).permitAll())
        .authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/register/**")).permitAll())
        .authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/document/**")).authenticated())
        .authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/admin/**")).authenticated())
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());


        http.addFilterBefore(jwtAuthnFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(adminFilter(), JWTAuthnFilter.class);
        return http.build();
    }

    @Bean
    DaoAuthenticationProvider authProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public ProviderManager authenticationManager(HttpSecurity security) throws Exception{
        return (ProviderManager) security.getSharedObject(AuthenticationManagerBuilder.class)
                                 .authenticationProvider(authProvider()).build();
    }

    @Bean
    public JWTAuthnFilter jwtAuthnFilter(){
        return new JWTAuthnFilter();
    }

    @Bean
    public AdminFilter adminFilter(){
        return new AdminFilter();
    }


    @Bean
    public UserDetailsService userDetailsService(){
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}


// Session management is a critical aspect of web applications, especially when dealing with user authentication and maintaining state across multiple requests. Let’s dive into the details:

// What Is a Session?
// A session represents a logical connection between a user and a web application.
// It starts when a user logs in or interacts with the application and continues until the user logs out or the session times out.
// During a session, the server maintains information about the user’s interactions, preferences, and security context.
// Why Is Session Management Important?
// User Authentication: Sessions are essential for tracking user authentication. When a user logs in, the server creates a session and associates it with the user.
// Stateful Interactions: Sessions allow applications to maintain state across multiple HTTP requests. For example, a shopping cart in an e-commerce site or remembering user preferences.
// Security Context: Sessions store information about the user’s security context (e.g., roles, permissions). This context is crucial for enforcing access control rules.
// Timeouts and Cleanup: Proper session management ensures that inactive sessions are eventually invalidated (timed out) to free up server resources.
// How Does Session Management Work?
// Session ID: When a user logs in, the server generates a unique session ID (usually a long random string). This ID is sent to the client (usually as a cookie).
// Server-Side Storage: The server maintains a data structure (often a hash map) that associates session IDs with session data (user-specific information).
// Session Data: Session data can include user details, preferences, and other relevant information.
// Session Timeout: Sessions have a timeout period. If the user doesn’t interact with the application within this time, the session is invalidated.
// Logout: When a user logs out, the session is explicitly invalidated, and the session data is removed.
// Common Session Management Techniques:
// Cookies: The most common way to manage sessions. The session ID is stored in a cookie on the client side.
// URL Rewriting: Less common now, but historically, session IDs were appended to URLs.
// Hidden Form Fields: Session IDs can be stored in hidden form fields in HTML forms.
// Server-Side Storage: The server maintains session data in memory (RAM), files, or databases.
// Stateless Sessions (JWT): In some cases (e.g., APIs), stateless sessions are used. JSON Web Tokens (JWT) carry session information in the token itself.
// Security Considerations:
// Session Hijacking: Protect against attackers stealing session IDs (e.g., through session fixation attacks).
// Session Expiration: Set appropriate session timeouts to balance security and usability.
// Secure Transmission: Ensure that session IDs are transmitted securely (e.g., over HTTPS).
// Session Revocation: Invalidate sessions when a user changes their password or logs out.