package org.example.furryfriendfund.user;

public interface IUsersService {
    UsersDTO registerUser(UsersDTO user);
    public void update(UsersDTO user);
}
