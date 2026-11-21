package datasource.mapper;

import datasource.entity.UserEntity;
import domain.model.user.User;

public class UserEntityMapper {

    public UserEntity toDataSource(User user) {
        if (user.getId() == null) {
            return new UserEntity(user.getLogin(), user.getPasswordHash(), user.getRoles());
        }
        return new UserEntity(user.getId(), user.getLogin(), user.getPasswordHash(), user.getRoles());
    }

    public User toDomain(UserEntity entity) {
        return new User(entity.getId(), entity.getLogin(), entity.getPasswordHash(), entity.getRoles());
    }
}