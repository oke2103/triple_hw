package com.triple.hw.dto;

import com.triple.hw.vo.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserPointDetail {
    public UUID placeId;
    public PointType pointType;
    public int point;
}
