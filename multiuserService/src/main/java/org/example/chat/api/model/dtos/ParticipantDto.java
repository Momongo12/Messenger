package org.example.chat.api.model.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto implements Serializable {

    private String chatId;
    private Long participantId;
    private String avatarImageUrl;
}
