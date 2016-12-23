package elections.service;

import elections.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static elections.domain.Game.GameStatus.*;
import static elections.domain.PartyType.*;
import static elections.domain.State.StateType.HOME_STATE;
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

    public Player startGame() {
        activePlayersCount++;
        if (activePlayersCount == 1) {
            game.setStatus(WAITING_OTHER_PLAYER);
            returnGameStatus();
            Player player = new Player();
            player.setParty(REPUBLICAN);
            return player;
        } else if (activePlayersCount == 2) {
            game.setStatus(GAME_STARTED);
            initGame();
            startTimer();
            Player player = new Player();
            player.setParty(DEMOCRAT);
            return player;
        } else if (activePlayersCount > 2) {
            game.setStatus(NO_FREE_CONNECTIONS);
            return null;
        }
        return null;
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

    public void movePoints(String fromAcr, String toAcr, PartyType party) {
        State from = states.get(fromAcr);
        State to = states.get(toAcr);

        if (from.getParty() != party || from.getScore().get() <= 1)
            return;

        if (from.getParty() == to.getParty() || to.getParty() == NOT_CAPTURED) {
            to.getScore().addAndGet(from.getScore().get() - 1);
            to.setParty(from.getParty());
            from.getScore().set(1);
        } else {
            AtomicLong toScore = to.getScore();
            AtomicLong fromScore = from.getScore();

            if (fromScore.get() >= toScore.get()) {
                toScore.set(fromScore.get() - toScore.get() - 1);
                fromScore.set(1);
                to.setParty(from.getParty());
            } else {
                toScore.set(toScore.get() - fromScore.get() - 1);
                fromScore.set(1);
            }
        }

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
