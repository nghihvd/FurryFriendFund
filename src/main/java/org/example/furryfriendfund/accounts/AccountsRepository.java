package org.example.furryfriendfund.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts,String> {
//JpaRepo sẽ tự động tạo ra các câu lệnh SQL ph hợp mà không cần viết tay
    @Query("select a from Accounts a where a.roleID = :roleID")
    Accounts findAdminByRoleID(@Param("roleID") int roleID);
    @Query(value = "SELECT * FROM accounts a WHERE a.total_donation >0", nativeQuery = true)
    List<Accounts> showDonator();




    @Query("SELECT COUNT(a) FROM Accounts a WHERE a.note = 'Available'")
    int countAccountsAvailable();

    @Query("SELECT COUNT(a) FROM Accounts a WHERE a.note = 'Banned'")
    int countAccountsBanned();

    @Query("SELECT COUNT(a) FROM Accounts a WHERE a.note = 'Waiting'")
    int countAccountsWaiting();

    @Query("SELECT COUNT(a) FROM Accounts a WHERE a.roleID = 1")
    int countAdminAccounts();

    @Query("SELECT COUNT(a) FROM Accounts a WHERE a.roleID = 2")
    int countStaffAccounts();

    @Query("SELECT COUNT(a) FROM Accounts a WHERE a.roleID = 3")
    int countMemberAccounts();

    @Query("select a from Accounts a where lower(a.accountID) = lower(:accountID)")
    Accounts findByAccountIDIgnoreCase(@Param(("accountID"))String id);


    @Query("SELECT a FROM Accounts a WHERE LOWER(a.accountID) LIKE LOWER(CONCAT('%', :accountID, '%'))")
    List<Accounts> findByAccountIDContainingIgnoreCase(@Param("accountID") String accountID);

    @Query("SELECT a FROM Accounts a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Accounts> findByNameContainingIgnoreCase(@Param("name") String name);

}
