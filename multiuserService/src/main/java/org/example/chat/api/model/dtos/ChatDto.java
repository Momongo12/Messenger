package org.example.chat.api.model.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ChatDto{

    private String id;
    private String name;
    private Long createdAt;
}
