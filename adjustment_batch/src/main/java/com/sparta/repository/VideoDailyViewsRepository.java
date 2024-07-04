package com.sparta.repository;

import com.sparta.entity.VideoDailyViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoDailyViewsRepository extends JpaRepository<VideoDailyViewsEntity, Long> {

    Optional<VideoDailyViewsEntity> findByVideoIdAndDate(Long videoId, LocalDate date);

    @Query("SELECT v.viewCount FROM video_daily_views v WHERE v.videoId = :videoId AND v.date = :date")
    Optional<Long> findViewCountByVideoIdAndDate(Long videoId, LocalDate date);

    @Query("SELECT SUM(v.viewCount) FROM video_daily_views v WHERE v.videoId = :videoId AND v.date < :date")
    Optional<Long> findTotalViewsByVideoIdBeforeDate(@Param("videoId") Long videoId, @Param("date") LocalDate date);
}
