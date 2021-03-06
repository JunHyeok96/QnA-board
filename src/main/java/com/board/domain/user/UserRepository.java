package com.board.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  User findByUserId(String userId);

  User findBySessionId(String sessionId);
}
