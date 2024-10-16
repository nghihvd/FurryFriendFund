package org.example.furryfriendfund.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.example.furryfriendfund.accounts.Accounts;
import org.example.furryfriendfund.accounts.LoggerDetail;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j // automated create logger and write log
public class JwtTokenProvider {

    // only server know
    private final String JWT_SECRET = "furryfund";

    // effecting time of jwt (MILLISECONDS)
    private final long JWT_EXPIRATION_TIME =  1000*60*60*24*7; // 7 DAYS

    /**
     *  create jwt from account information
     * @param loginAcc -> accountinfnformation in db
     * @return jwt
     */
    public String generateToken(LoggerDetail loginAcc) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_TIME);
        // Thêm thông tin role vào JWT claims
        // Thêm thông tin role vào JWT claims
        Map<String, Object> claims = new HashMap<>();
        System.out.println(loginAcc.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("roles", loginAcc.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        // create jwt from accountID
        return Jwts.builder() // initialize a builder to build jwt token
                .setSubject(loginAcc.getAccount().getAccountID()) // subject is used to identify user
                .claim("roles",claims.get("roles"))
                .setIssuedAt(now) // set time create token
                .setExpiration(expiryDate) // expired time
                .signWith(SignatureAlgorithm.HS512,JWT_SECRET) // using Hs512 to sign token
                .compact();
    }

    /**
     * get accountID from jwt
     * @param token jwt
     * @return value of subject in claims
     */
    public String getAccountIDFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET) // set secret key which have sign before
                .parseClaimsJws(token) // analyse jwt and return a object Jws<Claims> contains information in token
                .getBody(); // get the body og jwt where contains claims.
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch(MalformedJwtException e) {
            log.error("Invalid JWT token", e);
        } catch(ExpiredJwtException e) {
            log.error("Expired JWT token", e);
        } catch(UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
        } catch(IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
        }
        return false;
    }


}
