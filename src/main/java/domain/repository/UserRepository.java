package domain.repository;

import domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);
}
