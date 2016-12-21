package elections.service;

import elections.domain.Game;
import elections.domain.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpServerErrorException;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static elections.domain.Game.GameStatus.*;
import static elections.domain.State.DefaultStates.*;
import static elections.domain.State.StateParty.*;
import static elections.domain.State.StateType.*;

@Service
public class GameService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Long activePlayersCount = 0L;
    private Game game = new Game();
    private Logger log = LoggerFactory.getLogger(GameService.class);
    private ResourceLoader resourceLoader;

    public Game startGame() {
        activePlayersCount++;
        if (activePlayersCount == 1) {
            game.setStatus(WAITING_OTHER_PLAYER);
            returnGameStatus();
        } else if (activePlayersCount == 2) {
            game.setStatus(GAME_STARTED);
//            initGame();
            startTimer();
        } else if (activePlayersCount > 2) {
            game.setStatus(NO_FREE_CONNECTIONS);
        }
        return game;
    }

    private void initGame() {
        HashMap<String, State> states = new HashMap<>();
        try {
            Files.lines(Paths.get(getClass().getResource("resources/statesNames.txt").toURI()), StandardCharsets.UTF_8).
                forEach((String name) -> states.put(name, new State()));
        } catch (IOException|URISyntaxException e) {
            log.error("Невозможно загрузить список штатов");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Невозможно загрузить список штатов");
        }

        states.get(DEFAULT_DEMOCRAT_STATE.getStateName()).setParty(DEMOCRAT);
        states.get(DEFAULT_DEMOCRAT_STATE.getStateName()).setType(HOME_STATE);
        states.get(DEFAULT_REPUBLICAN_STATE.getStateName()).setParty(REPUBLICAN);
        states.get(DEFAULT_REPUBLICAN_STATE.getStateName()).setType(HOME_STATE);

        game.setStates(states);
    }

    private void startTimer() {
        new Timer(1000, this::tick).start();
    }

    private void tick(ActionEvent actionEvent) {
        returnGameStatus();
        log.info("Send game");
    }

    private void returnGameStatus() {
        simpMessagingTemplate.convertAndSend("/game/status", game);
    }
}
