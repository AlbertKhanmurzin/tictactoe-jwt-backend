package domain.model.exceptions;

import java.util.UUID;

public class GameOverException extends RuntimeException {
    public GameOverException(String message) {
        super(message);
    }
}
