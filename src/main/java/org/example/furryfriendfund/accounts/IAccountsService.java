package org.example.furryfriendfund.accounts;

import java.util.List;

public interface IAccountsService {
    Accounts saveAccountsInfo(Accounts user);
     boolean ckLogin(String userID,String password);
     Accounts getUserById(String userID);
    Accounts save(Accounts accounts);
    boolean delete(Accounts accounts);
    List<Accounts> showDonators();
    Accounts getAccByIdIgnoreCase(String id);
    List<Accounts> getAllAccounts();
    String ChangeStatus(Accounts accounts,String button);
}
