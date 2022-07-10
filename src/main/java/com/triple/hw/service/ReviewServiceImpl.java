package com.triple.hw.service;

import com.triple.hw.dto.ReviewRequestDto;
import com.triple.hw.dto.ReviewResponseDto;
import com.triple.hw.dto.UserPointDetail;
import com.triple.hw.dto.UserPointResponseDto;
import com.triple.hw.entity.PointEntity;
import com.triple.hw.entity.PointHistoryEntity;
import com.triple.hw.entity.ReviewEntity;
import com.triple.hw.repository.PointHistoryRepository;
import com.triple.hw.repository.PointRepository;
import com.triple.hw.repository.ReviewRepository;
import com.triple.hw.vo.PointType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 이후 요구사항에 의해 점수가 변경될 경우를 고려해서 점수 타입에 따라 점수 구분
     */
    public static final int DEFAULT_TEXT_POINT = 1;
    public static final int DEFAULT_PHOTO_POINT = 1;
    public static final int DEFAULT_FIRST_REVIEW_POINT = 1;

    @Override
    public ReviewResponseDto addReviewEvent(ReviewRequestDto requestDto) {

        // request 의 정보로 리뷰가 존재할 시 RuntimeException 발생.
        if (isExistReview(requestDto.getReviewId()) || isExistUserPlaceReview(requestDto.getUserId(), requestDto.getPlaceId())) {
            throw new RuntimeException("해당 리뷰는 존재합니다.");
        } else {
            ReviewEntity review = requestDto.toEntity();

            // 텍스트 리뷰가 존재할 경우 RuntimeException 발생.
            if (isExistUserPoint(review, PointType.TEXT)) {
                throw new RuntimeException("해당 리뷰의 텍스트 점수는 존재합니다.");
            } else {
                // request 의 content가 1자 이상 존재 시 점수 부여.
                if (review.getContent().length() > 0) {

                    // 텍스트 리뷰 점수 추가.
                    review.addPoint(PointType.TEXT, DEFAULT_TEXT_POINT);
                    // 텍스트 리뷰 점수 이력 추가.
                    addPointHistory(review, PointType.TEXT, DEFAULT_TEXT_POINT);
                }
            }

            // 리뷰에 첨부파일이 존재할 경우 RuntimeException 발생.
            if (isExistUserPoint(review, PointType.PHOTO)) {
                throw new RuntimeException("해당 리뷰의 사진 첨부 점수는 존재합니다.");
            } else {
                // request 의 첨부파일이 1개 이상 존재 시 점수 부여.
                if (review.getAttachFileEntityList().size() > 0) {

                    // 첨부파일 리뷰 점수 추가.
                    review.addPoint(PointType.PHOTO, DEFAULT_PHOTO_POINT);
                    // 첨부파일 리뷰 점수 이력 추가.
                    addPointHistory(review, PointType.PHOTO, DEFAULT_PHOTO_POINT);
                }
            }

            // 첫 리뷰 작성 시 첫 리뷰 점수 추가.
            if (isFirstReview(review)) {
                // 첫 리뷰 점수 추가
                review.addPoint(PointType.FIRST_REVIEW, DEFAULT_FIRST_REVIEW_POINT);
                // 첫 리뷰 점수 이력 추가.
                addPointHistory(review, PointType.FIRST_REVIEW, DEFAULT_FIRST_REVIEW_POINT);
            }
            ReviewEntity savedReview = reviewRepository.save(review);
            return savedReview.toReviewResponseDto();
        }
    }

    @Override
    public ReviewResponseDto modReviewEvent(ReviewRequestDto requestDto) {

        // request 의 reviewId로 리뷰가 존재하지 않을 시 RuntimeException 발생.
        if (isExistReview(requestDto.getReviewId())) {
            ReviewEntity findReview = reviewRepository.getByReviewId(requestDto.getReviewId());

            // 텍스트 리뷰 점수가 존재하고 수정된 컨텐츠가 내용이 없을 시 텍스트 점수 회수
            if (isExistUserPoint(findReview, PointType.TEXT) && requestDto.getContent().length() == 0){
                // 텍스트 리뷰 점수 회.
                Optional<PointEntity> any = findReview.getPointEntityList().stream().filter(n -> n.getPointType() == PointType.TEXT).findAny();
                findReview.getPointEntityList().remove(any.orElseThrow());

                // 텍스트 리뷰 점수 회수 이력 추가.
                addPointHistory(findReview, PointType.TEXT, (-1) * DEFAULT_TEXT_POINT);
            }

            // 리뷰 컨텐츠 내용 변경
            findReview.changeContent(requestDto.getContent());

            // 첨부파일 리뷰 점수가 존재하고 수정된 첨부파일이 없을 시 첨부파일 점수 회수
            if (isExistUserPoint(findReview, PointType.PHOTO) && requestDto.getAttachedPhotoIds().size() == 0) {

                // 첨부파일 리뷰 점수 회수.
                Optional<PointEntity> any = findReview.getPointEntityList().stream().filter(n -> n.getPointType() == PointType.PHOTO).findFirst();
                findReview.getAttachFileEntityList().remove(any.orElseThrow());

                // 첨부파일 리뷰 점수 회수 이력 추가.
                addPointHistory(findReview, PointType.PHOTO, (-1) * DEFAULT_PHOTO_POINT);
            }

            // 리뷰 첨부파일 내용 변경
            findReview.getAttachFileEntityList().clear();
            findReview.addAttachFileEntityList(requestDto.getAttachedPhotoIds());

            ReviewEntity savedReview = reviewRepository.save(findReview);
            return savedReview.toReviewResponseDto();

        } else {
            throw new RuntimeException("해당 리뷰는 존재하지 않습니다.)");
        }

    }

    @Override
    public ReviewResponseDto deleteReviewEvent(ReviewRequestDto requestDto) {

        // 리뷰가 존재하지 않을 시 RuntimeException 발생. ( 점수 회수 이력 추가 후 엔티티 삭제 )
        if (isExistReview(requestDto.getReviewId())) {
            ReviewEntity findReview = reviewRepository.getByReviewId(requestDto.getReviewId());

            // 텍스트 점수 존재할 경우 텍스트 점수 회수 이력 추가
            if (isExistUserPoint(findReview, PointType.TEXT)) {
                addPointHistory(findReview, PointType.TEXT, (-1) * DEFAULT_TEXT_POINT);
            }

            // 첨부파일 점수 존재할 경우 첨부파일 점수 회수 이력 추가
            if (isExistUserPoint(findReview, PointType.PHOTO)) {
                addPointHistory(findReview, PointType.PHOTO, (-1) * DEFAULT_PHOTO_POINT);
            }

            // 첫 리뷰 점수 존재할 경우 첫 리뷰 점수 회수 이력 추가
            if (isExistUserPoint(findReview, PointType.FIRST_REVIEW)) {
                addPointHistory(findReview, PointType.FIRST_REVIEW, (-1) * DEFAULT_FIRST_REVIEW_POINT);
            }

            reviewRepository.delete(findReview);
            return findReview.toReviewResponseDto();
        } else {
            throw new RuntimeException("해당 리뷰는 존재하지 않습니다.)");
        }

    }

    @Override
    public UserPointResponseDto findUserPoint(UUID userId) {
        List<UserPointDetail> userPointDetails = new ArrayList<>();

        List<PointEntity> pointByUserId = pointRepository.findByUserId(userId);
        pointByUserId.stream().forEach(n -> {
            userPointDetails.add(UserPointDetail.builder()
                    .placeId(n.getPlaceId())
                    .pointType(n.getPointType())
                    .point(n.getPoint())
                    .build());
        });

        return UserPointResponseDto.builder()
                .userId(userId)
                .point(pointByUserId.stream().mapToInt(PointEntity::getPoint).sum())
                .userPointDetails(userPointDetails)
                .build();
    }

    private void addPointHistory(ReviewEntity review, PointType pointType, int point) {
        pointHistoryRepository.save(PointHistoryEntity.builder()
                .pointId(UUID.randomUUID())
                .userId(review.getUserId())
                .placeId(review.getPlaceId())
                .pointType(pointType)
                .point(point)
                .build());
    }

    private boolean isFirstReview(ReviewEntity review) {
        ReviewEntity reviewByPlaceId = reviewRepository.getByPlaceId(review.getPlaceId());
        return reviewByPlaceId == null ? true : false;
    }

    private boolean isExistReview(UUID reviewId) {
        ReviewEntity reviewByReviewId = reviewRepository.getByReviewId(reviewId);
        return reviewByReviewId == null ? false : true;
    }

    private boolean isExistUserPlaceReview(UUID placeId, UUID userId) {
        ReviewEntity reviewByUserPlace = reviewRepository.getByPlaceIdAndUserId(placeId, userId);
        return reviewByUserPlace == null ? false : true;
    }

    private boolean isExistUserPoint(ReviewEntity review, PointType pointType) {
        List<PointEntity> pointEntityList = review.getPointEntityList();
        if (pointEntityList.size() == 0) {
            return false;
        }
        long count = pointEntityList.stream().filter(n -> n.getPointType() == pointType).count();
        return count > 0 ? true : false;

    }

}
