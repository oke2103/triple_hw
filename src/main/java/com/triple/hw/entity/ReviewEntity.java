package com.triple.hw.entity;

import com.triple.hw.dto.ReviewResponseDto;
import com.triple.hw.vo.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "REVIEW",
        indexes = {@Index(name = "REVIEW_CONSTRAINTS_INDEX", columnList = "placeId, userId", unique = true)}
)
public class ReviewEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID reviewId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID placeId;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachFileEntity> attachFileEntityList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointEntity> pointEntityList = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public void addAttachFileEntityList(List<UUID> attachFileList) {
        attachFileList.stream().forEach(n -> {
            AttachFileEntity attachFileEntity = AttachFileEntity.builder()
                    .attachFileId(n)
                    .review(this)
                    .build();

            attachFileEntityList.add(attachFileEntity);
        });
    }

    public void addPoint(PointType pointType, int point) {
        pointEntityList.add(PointEntity.builder()
                .pointId(UUID.randomUUID())
                .placeId(placeId)
                .userId(userId)
                .review(this)
                .pointType(pointType)
                .point(point)
                .build());
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public ReviewResponseDto toReviewResponseDto() {
        return ReviewResponseDto.builder()
                .reviewId(reviewId)
                .build();
    }

}

