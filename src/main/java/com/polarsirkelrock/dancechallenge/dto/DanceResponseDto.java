package com.polarsirkelrock.dancechallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DanceResponseDto {
    private boolean success;
    private String message;
}
