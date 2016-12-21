package elections.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    public enum DefaultStates{
        DEFAULT_DEMOCRAT_STATE("California"),
        DEFAULT_REPUBLICAN_STATE("Florida");

        String stateName;

        DefaultStates(String stateName) {
            this.stateName = stateName;
        }

        public String getStateName() {
            return stateName;
        }
    }

    StateParty party = StateParty.NOT_CAPTURED;
    StateType type = StateType.SIMPLE_STATE;

    int score = 0;
}
