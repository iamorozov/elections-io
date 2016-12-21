package elections.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Game {
    public enum GameStatus{
        WAITING_OTHER_PLAYER,
        GAME_STARTED,
        NO_FREE_CONNECTIONS
    }

    GameStatus status = GameStatus.WAITING_OTHER_PLAYER;
    Map<String, State> states = new HashMap<>();
}
