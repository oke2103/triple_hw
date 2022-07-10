package com.triple.hw.service;

import com.triple.hw.dto.ReviewRequestDto;
import com.triple.hw.dto.ReviewResponseDto;
import com.triple.hw.dto.UserPointResponseDto;

import java.util.UUID;

public interface ReviewService {
    ReviewResponseDto addReviewEvent(ReviewRequestDto reviewRequestDto);

    ReviewResponseDto modReviewEvent(ReviewRequestDto reviewRequestDto);

    ReviewResponseDto deleteReviewEvent(ReviewRequestDto reviewRequestDto);

    UserPointResponseDto findUserPoint(UUID userId);
}

