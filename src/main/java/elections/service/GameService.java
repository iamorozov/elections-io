package elections.service;

import elections.domain.Game;
import elections.domain.State;
import elections.domain.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.util.*;

import static elections.domain.Game.GameStatus.*;
import static elections.domain.State.StateParty.*;
import static elections.domain.State.StateType.*;
import static elections.domain.States.CALIFORNIA;
import static elections.domain.States.FLORIDA;

@Service
public class GameService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Long activePlayersCount = 0L;
    private Game game = new Game();
    private Logger log = LoggerFactory.getLogger(GameService.class);
    private Map<String, State> states = new HashMap<>();
    private List<State> homeStates = new ArrayList<>();

    public Game startGame() {
        activePlayersCount++;
        if (activePlayersCount == 1) {
            game.setStatus(WAITING_OTHER_PLAYER);
            returnGameStatus();
        } else if (activePlayersCount == 2) {
            game.setStatus(GAME_STARTED);
            initGame();
            startTimer();
        } else if (activePlayersCount > 2) {
            game.setStatus(NO_FREE_CONNECTIONS);
        }
        return game;
    }

    private void startTimer() {
        new Timer(1000, this::tick).start();
    }

    private void tick(ActionEvent actionEvent) {
        generateHomePoints();
        returnGameStatus();
        log.info("Send game");
    }

    private void generateHomePoints() {
        homeStates.forEach(state -> {
            state.getScore().incrementAndGet();
            game.getModifiedStates().put(state.getStateAcronym(), state);
        });
    }

    private void returnGameStatus() {
        simpMessagingTemplate.convertAndSend("/game/status", game);
        game.getModifiedStates().clear();
    }

    public void movePoints(State from, State to) {
        to.getScore().addAndGet(from.getScore().get());
        from.getScore().set(1);
        game.getModifiedStates().put(from.getStateAcronym(), from);
        game.getModifiedStates().put(to.getStateAcronym(), to);
    }

    private void initGame() {
        initStates();
    }

    private void initStates() {
        Arrays.stream(States.values())
            .forEach(state -> states.put(state.getAcronym(), new State(state.getAcronym())));

        State california = states.get(CALIFORNIA.getAcronym());
        State florida = states.get(FLORIDA.getAcronym());

        california.setParty(DEMOCRAT);
        california.setType(HOME_STATE);
        florida.setParty(REPUBLICAN);
        florida.setType(HOME_STATE);

        homeStates.add(california);
        homeStates.add(florida);
    }
}
