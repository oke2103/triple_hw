package com.triple.hw.repository;

import com.triple.hw.entity.PointEntity;
import com.triple.hw.vo.PointType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PointRepository extends JpaRepository<PointEntity, UUID> {
    List<PointEntity> findByUserId(UUID userId);

    PointEntity getByUserIdAndPlaceIdAndPointType(UUID placeId, UUID userId, PointType pointType);


}
