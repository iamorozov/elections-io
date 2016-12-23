package elections.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StateChangedState {
    String from;
    String to;
    PartyType party;
}
