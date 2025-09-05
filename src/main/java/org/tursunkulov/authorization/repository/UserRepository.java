package org.tursunkulov.authorization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tursunkulov.authorization.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> allUsers();

    Optional<User> findUserById(int id);

    void deleteById(int id);

    void deleteByUsername(String username);

    Optional<User> patchPhoneNumber(int id, String phoneNumber);

    Optional<User> patchEmail(int id, String email);

    void updateUserById(int id, User user);

    void updateUserByUsername(String username, User user);
}
