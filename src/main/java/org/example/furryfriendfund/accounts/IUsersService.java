package org.example.furryfriendfund.accounts;

public interface IUsersService {
    Users registerUser(Users user);
    public void update(Users user);
    public boolean ckLogin(String userID,String password);
    public void logout();
    public Users getUserById(String userID);
}
