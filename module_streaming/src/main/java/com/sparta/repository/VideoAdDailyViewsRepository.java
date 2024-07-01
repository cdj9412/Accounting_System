package com.sparta.repository;

import com.sparta.entity.VideoAdDailyViewsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Repository
public interface VideoAdDailyViewsRepository extends JpaRepository<VideoAdDailyViewsEntity, Long> {

    Optional<VideoAdDailyViewsEntity> findByVideoIdAndAdIdAndDate(Long videoId, Long adId, LocalDate today);

    @Transactional
    @Modifying
    @Query("UPDATE video_ad_daily_views v SET v.viewCount = v.viewCount + 1 WHERE v.videoId = :videoId AND v.adId = :adId AND v.date = :date")
    void incrementViewCount(@Param("videoId") Long videoId, @Param("adId") Long adId, @Param("date") LocalDate date);
}
