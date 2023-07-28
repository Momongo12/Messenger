package com.example.messenger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SmileysCategory {
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("smileys")
    private String[] smileys;
}
