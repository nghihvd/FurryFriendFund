package org.example.furryfriendfund.accounts;

public interface IAccountsService {
    Accounts saveAccountsInfo(Accounts user);
    public void update(Accounts user);
    public boolean ckLogin(String userID,String password);
    public void logout();
    public Accounts getUserById(String userID);
}
