package de.esko.dfs.message;

import de.esko.dfs.statemachine.Event;
import lombok.*;

@RequiredArgsConstructor
@Getter
@ToString
public class Command {

    private final int code;
    private final String description;
    private final String origin;
    private final Event event;

}
