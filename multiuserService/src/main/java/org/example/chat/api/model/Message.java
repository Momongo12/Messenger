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
public class Message implements Serializable {

    private String senderName;

    private Long senderId;

    private String text;

    @Builder.Default
    private Long createdAt = Instant.now().toEpochMilli();
}
