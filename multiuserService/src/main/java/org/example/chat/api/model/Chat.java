package org.example.chat.api.model;

import lombok.*;
import org.example.chat.util.RandomIdGenerator;

import java.io.Serializable;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Serializable {

    @Builder.Default
    private String id = RandomIdGenerator.generate();

    private String name;

    @Builder.Default
    private Long createdAt = Instant.now().toEpochMilli();
}
