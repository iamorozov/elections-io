package elections.service;

import elections.domain.Game;
import org.springframework.stereotype.Service;

import static elections.domain.Game.GameStatus.*;

@Service
public class GameService {

    private Long activePlayersCount = 0L;

    public Game startGame() {
        activePlayersCount++;
        Game game = new Game();
        if (activePlayersCount == 1) {
            game.setStatus(WAITING_OTHER_PLAYER);
        } else if (activePlayersCount == 2) {
            game.setStatus(GAME_STARTED);
        } else if (activePlayersCount > 2) {
            game.setStatus(NO_FREE_CONNECTIONS);
        }
        return game;
    }
}
