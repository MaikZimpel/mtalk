package de.esko.dfs.message;

import lombok.*;

@RequiredArgsConstructor
@Getter
@ToString
public class Command {

    private final int code;
    private final String description;
    private final String origin;
    private final String event;

}
