package org.example.furryfriendfund.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
// Spring Security use UserDetails to save all information of user
public class LoggerDetail implements UserDetails {
    private Accounts account;


    /**
     * Collections.singletonList -> create a list only contain 1 authority is 'ROLE_USER'
     * @return list of authorities that user have
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        int roleID = account.getRoleID();
        return Collections.singletonList(new SimpleGrantedAuthority(String.valueOf(roleID)));
    }


    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getAccountID();
    }

    /**
     * check that login account is expired or not
     * @return true -> account not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * check account of user is locked or not
     * @return TRUE -> account not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        if(account.getNote().equals( "Available")){
            return true;
        }
        return false;
    }

    /**
     * check password is expired or not
     * @return true -> not expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * check account is enable or not
     * @return tru -> enable
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
