package com.sparta.repository;

import com.sparta.entity.VideoAdDailyViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoAdDailyViewsRepository extends JpaRepository<VideoAdDailyViewsEntity, Long> {
    List<VideoAdDailyViewsEntity> findByVideoIdAndDate(Long videoId, LocalDate date);

    @Query("SELECT SUM(v.viewCount) FROM video_ad_daily_views v WHERE v.videoId = :videoId AND v.date < :date")
    Optional<Long> findTotalAdViewsByVideoIdBeforeDate(@Param("videoId") Long videoId, @Param("date") LocalDate date);
}
