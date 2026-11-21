package domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserWinRate {
    private final UUID userId;
    private final String login;
    private final double winRate;

    public UserWinRate(UUID userId, double winRate) {
        this(userId, null, winRate);
    }
}