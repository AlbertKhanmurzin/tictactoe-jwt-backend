package domain.model.exceptions;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException{
    public GameNotFoundException(UUID id) {
        super("The game was not found: " + id);
    }
}
