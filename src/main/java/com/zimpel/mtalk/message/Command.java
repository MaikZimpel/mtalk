package com.zimpel.mtalk.message;

import lombok.*;

@RequiredArgsConstructor
@Getter
@ToString
public class Command {

    private final int code;
    private final String description;
    private final String mid;

}
