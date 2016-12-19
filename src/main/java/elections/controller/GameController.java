package elections.controller;

import elections.domain.Game;
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
    @SendTo("/game/status")
    public Game startGame() throws Exception {
        return gameService.startGame();
    }
}
