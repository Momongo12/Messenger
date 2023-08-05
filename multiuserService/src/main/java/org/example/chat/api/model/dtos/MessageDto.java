package org.example.chat.api.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private String senderName;

    private Long senderId;

    private String text;

    @Builder.Default
    private Long createdAt = Instant.now().toEpochMilli();
}
