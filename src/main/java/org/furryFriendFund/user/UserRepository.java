package org.furryFriendFund.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UsersDTO, String> {

}
