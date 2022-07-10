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
@Table(name = "ATTACH_FILE", uniqueConstraints = @UniqueConstraint(name = "ATTACH_FILE_UNIQUE", columnNames = {"attachFileId"}))
public class AttachFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachFileKey;

    @Column(columnDefinition = "BINARY(16)")
    private UUID attachFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewKey")
    private ReviewEntity review;

    @CreatedDate
    private LocalDateTime createdDate;

}


