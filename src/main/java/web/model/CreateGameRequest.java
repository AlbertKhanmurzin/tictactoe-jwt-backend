package web.model;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateGameRequest {
    private UUID id;
    private Boolean vsBot;
}
