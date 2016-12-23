package elections.domain;

import lombok.*;

import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
@NoArgsConstructor
public class State {

    public enum StateType{
        HOME_STATE,
        KEY_STATE,
        SIMPLE_STATE
    }

    public State(String stateAcronym) {
        this.stateAcronym = stateAcronym;
    }

    PartyType party = PartyType.NOT_CAPTURED;
    StateType type = StateType.SIMPLE_STATE;
    String stateAcronym;

    AtomicLong score = new AtomicLong(0);
}
