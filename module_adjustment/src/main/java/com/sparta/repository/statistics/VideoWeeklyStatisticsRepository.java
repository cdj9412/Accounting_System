package com.sparta.repository.statistics;

import com.sparta.entity.statistics.VideoMonthlyStatisticsEntity;
import com.sparta.entity.statistics.VideoWeeklyStatisticsEntity;
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
public interface VideoWeeklyStatisticsRepository extends JpaRepository<VideoWeeklyStatisticsEntity, Long> {

    @Query("SELECT v FROM video_weekly_statistics v WHERE v.weekStartDate = :startDate AND v.weekEndDate = :endDate ORDER BY v.viewCount DESC LIMIT 5")
    List<VideoWeeklyStatisticsEntity> findViewTop5(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v FROM video_weekly_statistics v WHERE v.weekStartDate = :startDate AND v.weekEndDate = :endDate ORDER BY v.playTime DESC LIMIT 5")
    List<VideoWeeklyStatisticsEntity> findTimeTop5(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
