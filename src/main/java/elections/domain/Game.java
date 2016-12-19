package elections.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    public enum GameStatus{
        WAITING_OTHER_PLAYER,
        GAME_STARTED,
        NO_FREE_CONNECTIONS
    }

    GameStatus status = GameStatus.WAITING_OTHER_PLAYER;
}
