package web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserResponse {
    private UUID uuid;
    private String login;
}
