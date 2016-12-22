package elections.controller;

import elections.domain.Game;
import elections.domain.State;
import elections.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @MessageMapping("/start")
    public void startGame() throws Exception {
        gameService.startGame();
    }

    @MessageMapping("/state-changed")
    public void stateChanged(State from, State to) {
        gameService.movePoints(from, to);
    }
}
