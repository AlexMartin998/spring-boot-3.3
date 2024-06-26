package com.example.demo.auth.jwt;

import com.example.demo.auth.service.CustomUserDetailsService;
import com.example.demo.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component // register as @Bean  -  semanticamente correcto xq no es Service, repo, etc...
@RequiredArgsConstructor // DI final fields
public class JwtAuthFilter extends OncePerRequestFilter { // to be executed once per request

    // construira constructor con cada property(FINAL) q le creemos a la clase y permitira la Inject en Auto
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final SecurityErrorResponse securityErrorResponse;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,     // req como tal - aqui la interceptamos
            @NonNull HttpServletResponse response,   // res como tal
            @NonNull FilterChain filterChain         // continuara con la ejecucion de los demas filtros de la filterChain
    ) throws ServletException, IOException {
        try {
            final String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                String userEmail = jwtService.extractUsername(jwt); // username 'cause jwt call it that way (email, uuid, username, etc...)

                // // si ya esta auth NO debo actualizar el SecurityContextHolder ni demas cosas
                // si !== null significa q ya esta auth
                if (StringUtils.hasText(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

                    // validate JWT
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // this object is needed to UPD the SecurityContextHolder
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // // Upd SecurityContextHolder
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        } catch (Exception e) {
            // handling Filter exceptions that can't be caught by GlobalExceptionHandler
            securityErrorResponse.sendErrorResponse(
                    request,
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage()
            );
            return;
        }

        // continue with the filter chain
        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String currentUri = request.getRequestURI();

        return jwtService.validateJwtRequest(authHeader, currentUri);
    }
}
