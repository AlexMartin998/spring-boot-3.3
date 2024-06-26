package com.example.demo.auth.service;

import com.example.demo.auth.jwt.JwtProperties;
import com.example.demo.shared.exceptions.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service    // transform to a managed @Bean of Spring (Inject)
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;


    public String extractUsername(String jwt) {
        // el Subject deberia ser el   (email,uuid,username)    q vamos a setear en la construccion del JWT
        return extractClaim(jwt, Claims::getSubject);
    }


    // // // generate JWT  --  sobrecarga de methods - Polymorphism
    public String generateJwt(Map<String, Object> extraClaims, UserDetails userDetails) {
        // extracalims es lo extra q quiero pasarle al payload del jwt

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())  // payload q se va a codificar (email, uuid, usrname)
                .setIssuedAt(new Date(System.currentTimeMillis()))    // when this jwt was created - to calculate the expiration date
                .setExpiration(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * jwtProperties.getExpiration())))    // setear el tiempo de validez del jwt
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)   // hashcode redomendado
                .compact();
    }

    // Polymorphism: Without any extraClaims
    public String generateJwt(UserDetails userDetails) {
        return generateJwt(new HashMap<>(), userDetails);
    }


    // // Validate JWT: valida jwt y q el (email,uuid,username) del subject pertenezca a ese usuario <-- en este contexto username=email
    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);

        return (
                username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(jwt)
        );
    }


    // // validate uri and bearer token
    public String validateJwtRequest(String authHeader, String uri) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }

        // // we need to handle like this 'cause of the filterChain behavior (says that this endpoint exists but need auth, another just handling by EntryPoint)
        // // Not is possible to set an 404, so this validation is not necessary, but to have 2 errors  <-- .anyRequest().authenticated()
        // regex for any api version
        String regex = "/api/v\\d+/auth/renew-token";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(uri);

        if (!StringUtils.hasText(authHeader) && matcher.find()) {
            throw new UnauthorizedException("Unauthorized");
        }

        return null;
    }


    // // generic functions
    // genericos para extraer cualquier claim q nos interese
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);    // extra TODOS los claims del JWT q se le pase
    }


    private Claims extractAllClaims(String jwt) {
        Claims claims;

        try {
            claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey()) // JWT Secret
                    .build()    // xq es 1 builder
                    .parseClaimsJws(jwt)    // parse el JWT para extraer los claims
                    .getBody();     // cuando hace el parse puede obtener los claims y en este caso queremos el body
        } catch (SecurityException ex) {    // invalid signature
            throw new UnauthorizedException("Invalid Token Signature");
        } catch (MalformedJwtException | UnsupportedJwtException ex) {
            throw new UnauthorizedException("Invalid Token");
        } catch (ExpiredJwtException ex) {
            throw new UnauthorizedException("Expired Token");
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid Token Claims");
        }

        return claims;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());   // jwt lo requiere en b64
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date()); // before 'cause the expiration is the SUM of now and JWT_EXPIRATION_HOURS
    }

}
