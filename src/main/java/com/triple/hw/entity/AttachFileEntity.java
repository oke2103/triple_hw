package com.triple.hw.entity;

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
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "ATTACH_FILE")
public class AttachFileEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID attachFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId", foreignKey = @ForeignKey(name = "fk_attachFile_to_review"))
    private ReviewEntity review;

    @CreatedDate
    private LocalDateTime createdDate;

}


