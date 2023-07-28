package com.example.messenger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Smileys {
    @JsonProperty("smileys")
    private SmileysCategory[] smileysCategories;
}
