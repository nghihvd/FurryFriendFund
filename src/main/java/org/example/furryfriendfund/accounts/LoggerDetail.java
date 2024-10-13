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
        String roleName = getRoleNameFromId(account.getRoleID());
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    // Phương thức để chuyển roleID thành role name
    private String getRoleNameFromId(int roleID) {
        switch (roleID) {
            case 1:
                return "ROLE_ADMIN";
            case 2:
                return "ROLE_STAFF";
            // Thêm các case cho các role khác nếu cần
            default:
                return "ROLE_MEMBER"; // Role mặc định nếu không xác định được
        }
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
        return true;
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
