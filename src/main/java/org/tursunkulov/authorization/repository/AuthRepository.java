package org.tursunkulov.authorization.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tursunkulov.authorization.entity.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Integer> {
  Optional<User> findByUsernameAndPassword(String username, String password);
}
