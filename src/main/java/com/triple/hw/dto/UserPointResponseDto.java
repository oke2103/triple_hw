package com.triple.hw.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserPointResponseDto {
    public UUID userId;
    public int point;
    public List<UserPointDetail> userPointDetails;
}
