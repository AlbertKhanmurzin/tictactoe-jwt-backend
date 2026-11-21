package domain.service.impl;

import domain.model.exceptions.UserAlreadyExistsException;
import domain.model.user.Role;
import domain.model.user.User;
import domain.repository.UserRepository;
import domain.service.UserService;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(String login, String passwordHash) {
        if (userRepository.existsByLogin(login)) {
            throw new UserAlreadyExistsException("Пользователь с логином '" + login + "' уже существует");
        }

        User newUser = new User(login, passwordHash, Set.of(Role.USER));
        userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }
}