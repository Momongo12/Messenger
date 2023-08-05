package org.example.chat.api.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant implements Serializable {
    @Builder.Default
    private Long enterAt = Instant.now().toEpochMilli();

    private Long participantId;

    private String avatarImageUrl;

    private String chatId;
}
