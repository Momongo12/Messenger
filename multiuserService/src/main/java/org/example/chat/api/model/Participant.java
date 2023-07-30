package org.example.chat.api.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    @Builder.Default
    Long enterAt = Instant.now().toEpochMilli();

    String id;

    String sessionId;

    String chatId;
}
