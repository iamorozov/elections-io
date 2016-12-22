package elections.domain;

import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
@NoArgsConstructor
public class State {

    public enum StateParty{
        DEMOCRAT,
        REPUBLICAN,
        NOT_CAPTURED
    }

    public enum StateType{
        HOME_STATE,
        KEY_STATE,
        SIMPLE_STATE
    }

    public State(String stateAcronym) {
        this.stateAcronym = stateAcronym;
    }

    StateParty party = StateParty.NOT_CAPTURED;
    StateType type = StateType.SIMPLE_STATE;
    String stateAcronym;

    AtomicLong score = new AtomicLong(0);
}
