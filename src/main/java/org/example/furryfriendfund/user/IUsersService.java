package org.example.furryfriendfund.user;

public interface IUsersService {
    Users registerUser(Users user);
    public void update(Users user);
    public boolean ckLogin(String userID,String password);
    public Users getUserById(String userID);
}
