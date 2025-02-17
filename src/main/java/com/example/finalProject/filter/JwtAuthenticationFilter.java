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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        try {
            // Check if Authorization header contains a Bearer token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // Validate token
                if (jwtTokenUtil.validateToken(token)) {
                    String username = jwtTokenUtil.extractUsername(token);

                    // Retrieve roles/authorities from the token (if applicable)
                    String roles = jwtTokenUtil.extractRoles(token); // Assuming you have this method
                    SimpleGrantedAuthority authorities = new SimpleGrantedAuthority(roles);
                    // Set authentication in the conte
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authorities));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // Log the exception and clear the SecurityContext
            ex.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        // Proceed with the next filter
        filterChain.doFilter(request, response);
    }
}

