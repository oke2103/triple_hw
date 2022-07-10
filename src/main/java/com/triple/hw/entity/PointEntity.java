package com.triple.hw.entity;

import com.triple.hw.vo.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "POINT"
        , uniqueConstraints = @UniqueConstraint(name = "POINT_UNIQUE", columnNames = {"userId", "placeId", "pointType"})
)
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointKey;

    @Column(columnDefinition = "BINARY(16)")
    private UUID pointId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID placeId;

    @Enumerated(EnumType.STRING)
    private PointType pointType;

    private int point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewKey")
    private ReviewEntity review;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
