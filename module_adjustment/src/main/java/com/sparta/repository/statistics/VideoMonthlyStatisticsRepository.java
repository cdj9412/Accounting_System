package com.sparta.repository.statistics;

import com.sparta.entity.statistics.VideoDailyStatisticsEntity;
import com.sparta.entity.statistics.VideoMonthlyStatisticsEntity;
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
public interface VideoMonthlyStatisticsRepository extends JpaRepository<VideoMonthlyStatisticsEntity, Long> {

    @Query("SELECT v FROM video_monthly_statistics v WHERE v.monthStartDate = :startDate AND v.monthEndDate = :endDate ORDER BY v.viewCount DESC LIMIT 5")
    List<VideoMonthlyStatisticsEntity> findViewTop5(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT v FROM video_monthly_statistics v WHERE v.monthStartDate = :startDate AND v.monthEndDate = :endDate ORDER BY v.playTime DESC LIMIT 5")
    List<VideoMonthlyStatisticsEntity> findTimeTop5(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
