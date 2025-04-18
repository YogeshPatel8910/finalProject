package com.example.finalProject.filter;

import com.example.finalProject.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Filter for JWT-based authentication
 * Intercepts HTTP requests to validate JWT tokens and set authentication in Spring Security context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    /**
     * Constructor for dependency injection
     *
     * @param jwtTokenUtil Utility for JWT token operations
     */
    @Autowired
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Core filter method that processes each HTTP request exactly once
     * Extracts and validates JWT token from Authorization header
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain for passing request to next filter
     * @throws ServletException If servlet exception occurs
     * @throws IOException If I/O exception occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        try {
            // Check if Authorization header contains a Bearer token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extract token by removing "Bearer " prefix
                String token = authHeader.substring(7);

                // Validate token signature and expiration
                if (jwtTokenUtil.validateToken(token)) {
                    // Extract username from token
                    String username = jwtTokenUtil.extractUsername(token);

                    // Extract roles/authorities from the token
                    String roles = jwtTokenUtil.extractRoles(token);
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roles);

                    // Create authentication object and set in security context
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,  // principal
                                    null,      // credentials (not needed after authentication)
                                    Collections.singleton(authority) // authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // Log the exception and clear the security context
            LOGGER.log(Level.SEVERE, "JWT Authentication failed", ex);
            SecurityContextHolder.clearContext();
        }

        // Continue with filter chain regardless of authentication result
        filterChain.doFilter(request, response);
    }
}