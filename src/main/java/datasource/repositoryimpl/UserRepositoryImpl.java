package datasource.repositoryimpl;

import datasource.mapper.UserEntityMapper;
import domain.model.user.User;
import domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(User user) {
        jpaRepository.save(mapper.toDataSource(user));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return jpaRepository.findByLogin(login).map(mapper::toDomain);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }
}