package it.smartcitywastemanagement.citizenms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static it.smartcitywastemanagement.citizenms.security.SecurityConstants.JWT_SECRET;
import static it.smartcitywastemanagement.citizenms.security.SecurityConstants.THIS_MICROSERVICE;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtilities jwtUtilities;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // mando una richiesta e mi prendo un header che si chiama "Authorization"
        final String authorizationHeader = request.getHeader("Authorization");

        String jwtToken = null;

        // poi ci prendiamo il valore di questo header, che è: Bearer <spazio> TOKEN
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            System.out.println("SONO NELL'IF");
            jwtToken = authorizationHeader.substring(7); // mi prendo il token (dal 7o carattere in poi)
        }


        if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        System.out.println(jwtToken);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();


            // Crea un oggetto UserDetails con il ruolo estratto dal token

            List<String> audience = claims.get("aud", List.class);
            if (audience != null && audience.contains(THIS_MICROSERVICE)) {
                String username = jwtUtilities.extractUsername(jwtToken);
                System.out.println(username);
                String role = claims.get("role", String.class);
                System.out.println(role);

                UserDetails userDetails = User.builder()
                        .username(username)
                        .password("")
                        .roles(role)
                        .build();

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                System.out.println(userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }
        chain.doFilter(request, response);
    }


}
