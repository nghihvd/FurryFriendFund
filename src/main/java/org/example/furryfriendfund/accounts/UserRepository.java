package org.example.furryfriendfund.accounts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,String> {
//JpaRepo sẽ tự động tạo ra các câu lệnh SQL ph hợp mà không cần viết tay
    
}
