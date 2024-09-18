package org.example.furryfriendfund.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts,String> {
//JpaRepo sẽ tự động tạo ra các câu lệnh SQL ph hợp mà không cần viết tay
    
}
