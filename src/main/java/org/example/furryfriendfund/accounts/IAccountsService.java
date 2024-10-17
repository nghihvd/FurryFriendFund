package org.example.furryfriendfund.accounts;

import java.util.List;

public interface IAccountsService {
    Accounts saveAccountsInfo(Accounts user);
    public boolean ckLogin(String userID,String password);
    public Accounts getUserById(String userID);
    Accounts save(Accounts accounts);
    boolean delete(Accounts accounts);
    List<Accounts> showDonators();
}
