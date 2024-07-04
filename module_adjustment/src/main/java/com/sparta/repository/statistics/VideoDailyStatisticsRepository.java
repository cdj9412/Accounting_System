package com.sparta.repository.statistics;

import com.sparta.entity.statistics.VideoDailyStatisticsEntity;
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

    @Query("SELECT v FROM video_daily_statistics v WHERE v.date = :today ORDER BY v.viewCount DESC LIMIT 5")
    List<VideoDailyStatisticsEntity> findViewTop5(@Param("today") LocalDate today);

    @Query("SELECT v FROM video_daily_statistics v WHERE v.date = :today ORDER BY v.playTime DESC LIMIT 5")
    List<VideoDailyStatisticsEntity> findTimeTop5(@Param("today") LocalDate today);

}
