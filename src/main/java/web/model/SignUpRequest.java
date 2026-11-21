package web.model;

import lombok.Data;

@Data
public class SignUpRequest {
    private final String login;
    private final String password;
}
