package com.sparta.repository.daily;

import com.sparta.entity.daily.VideoDailyStatisticsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoDailyStatisticsRepository extends JpaRepository<VideoDailyStatisticsEntity, Long> {
    @Query("SELECT v FROM video_daily_statistics v WHERE v.videoId = :videoId AND v.date = CURRENT_DATE")
    Optional<VideoDailyStatisticsEntity> findByDate(@Param("videoId") Long videoId);

    @Query("SELECT v FROM video_daily_statistics v WHERE v.videoId = :videoId AND (v.date BETWEEN :monday AND :sunday)")
    List<VideoDailyStatisticsEntity> findPeriodData(@Param("videoId") Long videoId, @Param("monday") LocalDate monday, @Param("sunday") LocalDate sunday);

    @Modifying
    @Transactional
    @Query("UPDATE video_daily_statistics v SET v.viewCount = :viewCount, v.playTime = :playTime WHERE v.videoId = :videoId AND v.date = :date")
    void updateDailyStatistics(@Param("videoId") Long videoId, @Param("date") LocalDate date, @Param("viewCount") Long viewCount, @Param("playTime") Long playTime);
}
