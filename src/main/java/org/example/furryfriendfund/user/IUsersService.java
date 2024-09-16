package org.example.furryfriendfund.user;

public interface IUsersService {
    Users saveUser(Users user);
    public boolean ckLogin(String userID,String password);
    public void logout();
    public Users getUserById(String userID);
}
