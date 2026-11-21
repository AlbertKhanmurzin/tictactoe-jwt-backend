package domain.service;

import domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    void createUser(String login, String passwordHash);
    Optional<User> findByLogin(String login);
    Optional<User> findById(UUID id);
    boolean existsByLogin(String login);
}
