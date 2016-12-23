package elections.controller;

import elections.domain.StateChangedState;
import elections.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameStateController {

    @Autowired
    private GameService gameService;

    @MessageMapping("/state-changed")
    public void stateChanged(StateChangedState message) {
        gameService.movePoints(message.getFrom(), message.getTo(), message.getParty());
    }
}
