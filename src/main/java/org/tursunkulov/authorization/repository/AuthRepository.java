package org.tursunkulov.authorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tursunkulov.authorization.entity.User;

@Repository
public interface AuthRepository extends JpaRepository<User, String> {
    void saveUser(User user);

    void checkUser(String username, String password);
}
