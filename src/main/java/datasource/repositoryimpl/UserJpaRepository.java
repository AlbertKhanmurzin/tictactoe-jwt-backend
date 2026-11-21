package datasource.repositoryimpl;

import datasource.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends CrudRepository<UserEntity, UUID>{

    boolean existsByLogin(String login);
    Optional<UserEntity> findByLogin(String login);
}
