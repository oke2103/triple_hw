package com.triple.hw.apicontroller;

import com.triple.hw.dto.ReviewRequestDto;
import com.triple.hw.dto.ReviewResponseDto;
import com.triple.hw.dto.UserPointResponseDto;
import com.triple.hw.service.ReviewService;
import com.triple.hw.vo.EventAction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/events")
    public ResponseEntity<ReviewResponseDto> reviewEvent(@RequestBody ReviewRequestDto reviewRequestDto) {
        ReviewResponseDto reviewResponseDto = null;
        try {
            EventAction action = reviewRequestDto.getAction();
            if (isAddReview(action)) {
                reviewResponseDto = reviewService.addReviewEvent(reviewRequestDto);
            } else if (isModMethod(action)) {
                reviewResponseDto = reviewService.modReviewEvent(reviewRequestDto);
            } else if (isDeleteMethod(action)) {
                reviewResponseDto = reviewService.deleteReviewEvent(reviewRequestDto);
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(reviewResponseDto);
    }

    @GetMapping("/{userId}/point")
    public UserPointResponseDto getUserPoint(@PathVariable UUID userId) {
        return reviewService.findUserPoint(userId);
    }

    public boolean isAddReview(EventAction action) {
        return action == EventAction.ADD ? true : false;
    }

    public boolean isModMethod(EventAction action) {
        return action == EventAction.MOD ? true : false;
    }

    public boolean isDeleteMethod(EventAction action) {
        return action == EventAction.DELETE ? true : false;
    }

}
