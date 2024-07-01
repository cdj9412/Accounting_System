package com.sparta.repository;

import com.sparta.entity.VideoAdDailyViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoAdDailyViewsRepository extends JpaRepository<VideoAdDailyViewsEntity, Long> {
    Optional<VideoAdDailyViewsEntity> findByVideoIdAndAdId(Long videoId, Long adId);
}
