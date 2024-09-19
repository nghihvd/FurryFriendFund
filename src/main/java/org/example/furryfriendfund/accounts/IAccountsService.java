package org.example.furryfriendfund.accounts;

public interface IAccountsService {
    Accounts saveAccountsInfo(Accounts user);
    public boolean ckLogin(String userID,String password);
    public Accounts getUserById(String userID);
}
