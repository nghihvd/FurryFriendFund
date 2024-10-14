package org.example.furryfriendfund.jwt;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlackListService {
    private final Set<String> blackListedTokens = new HashSet<String>();
    public void addTokenToBlackList(String token){
        blackListedTokens.add(token);
    }

    public boolean isTokenBlackListed(String token){
        return blackListedTokens.contains(token);
    }

}
