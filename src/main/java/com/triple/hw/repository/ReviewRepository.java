package com.triple.hw.repository;

import com.triple.hw.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    ReviewEntity getByPlaceId(UUID placeId);

    ReviewEntity getByPlaceIdAndUserId(UUID placeId, UUID userId);

    ReviewEntity getByReviewId(UUID reviewId);

    List<ReviewEntity> findAllByUserIdAndPlaceId(UUID userId, UUID placeId);

    List<ReviewEntity> findAllByPlaceId(UUID placeId);

}
