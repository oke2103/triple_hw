package com.triple.hw.dto;

import com.triple.hw.entity.ReviewEntity;
import com.triple.hw.vo.EventAction;
import com.triple.hw.vo.EventType;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewRequestDto {

    private EventType type;
    private EventAction action;
    private UUID reviewId;
    private String content;
    private List<UUID> attachedPhotoIds;
    private UUID userId;
    private UUID placeId;

    public ReviewEntity toEntity() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewId(reviewId)
                .content(content)
                .userId(userId)
                .placeId(placeId)
                .build();
        review.addAttachFileEntityList(attachedPhotoIds);
        return review;
    }
}
