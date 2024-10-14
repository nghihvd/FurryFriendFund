package org.example.furryfriendfund.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.furryfriendfund.accounts.AccountsService;
import org.example.furryfriendfund.accounts.LoggerDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TokenBlackListService tokenBlackListService;

    @Autowired
    protected AccountsService accountsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{

            String jwt = getJwtFromRequest(request);
            if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt) && !tokenBlackListService.isTokenBlackListed(jwt)){
                //get accountID from jwt
                String accountID = tokenProvider.getAccountIDFromJWT(jwt);

                //get account information from id
                UserDetails userDetails = accountsService.loadUserByUsername(accountID);

                if(userDetails != null ){
                    // if account is available
                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch(Exception e){
            log.error("Failled on set account authentication",e);
        }
        filterChain.doFilter(request, response);
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // check header Authorization contains jwt ìnformation or not
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // get token but not Bearer
        }
        return null;
    }



}
