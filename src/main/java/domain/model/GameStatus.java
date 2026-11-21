package domain.model;

public enum GameStatus{
    WAITING_FOR_PLAYERS, // Игра создана и ждет подключения второго игрока
    PLAYING,             // Игра идет (очередь хода определяется по currentPlayerId)
    X_WON,               // Победил крестик (playerXId)
    O_WON,               // Победил нолик (playerOId)
    DRAW                 // Ничья: все клетки заполнены, победителя нет
}