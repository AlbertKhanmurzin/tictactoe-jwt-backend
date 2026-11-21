package di;

import datasource.mapper.GameEntityMapper;
import datasource.mapper.UserEntityMapper;
import datasource.repositoryimpl.GameJpaRepository;
import datasource.repositoryimpl.GameRepositoryImpl;
import datasource.repositoryimpl.UserJpaRepository;
import datasource.repositoryimpl.UserRepositoryImpl;
import domain.repository.UserRepository;
import domain.service.AuthService;
import domain.service.UserService;
import domain.service.impl.AuthServiceImpl;
import domain.service.impl.GameServiceImpl;
import domain.repository.GameRepository;
import domain.service.GameService;
import domain.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.mapper.GameWebMapper;
import web.mapper.SignUpRequestMapper;
import web.security.JwtProvider;

@Configuration
public class AppConfig {

    // 1. мапперы данных
    @Bean
    public GameEntityMapper gameEntityMapper() {
        return new GameEntityMapper();
    }

    @Bean
    public UserEntityMapper userEntityMapper() {
        return new UserEntityMapper();
    }

    @Bean
    public GameWebMapper gameWebMapper() {
        return new GameWebMapper();
    }

    @Bean
    public SignUpRequestMapper signUpRequestMapper() {
        return new SignUpRequestMapper();
    }

    // 2. репозитории: связываем абстрактные доменные интерфейсы с JPA реализацией в datasource
    @Bean
    public GameRepository gameRepository(GameJpaRepository gameJpaRepository, GameEntityMapper mapper) {
        return new GameRepositoryImpl(gameJpaRepository, mapper);
    }

    @Bean
    public UserRepository userRepository(UserJpaRepository userJpaRepository, UserEntityMapper mapper) {
        return new UserRepositoryImpl(userJpaRepository, mapper);
    }

    // 3. бин для безопасного хеширования паролей (BCrypt с фактором затрат 12)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // 4. сервисы бизнес-логики:
    // UserService управляет пользователями в БД
    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    // AuthService выполняет регистрацию и валидацию Basic Auth через UserService
    @Bean
    public AuthService authService(UserService userService, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        return new AuthServiceImpl(userService, passwordEncoder, jwtProvider);
    }

    // GameService управляет ходами, ботом и статусами игры
    @Bean
    public GameService gameService(GameRepository gameRepository) {
        return new GameServiceImpl(gameRepository);
    }
}
