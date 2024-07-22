package com.sparta.repository.statistics;

import com.sparta.entity.statistics.VideoDailyStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VideoDailyStatisticsRepository extends JpaRepository<VideoDailyStatisticsEntity, Long> {

    @Query("SELECT DISTINCT vds FROM video_daily_statistics vds " +
            "JOIN vds.video v " +
            "WHERE vds.date = :today AND v.creatorId = :userId " +
            "ORDER BY vds.viewCount DESC LIMIT 5")
    List<VideoDailyStatisticsEntity> findViewTop5(@Param("userId") String userId, @Param("today") LocalDate today);

    @Query("SELECT vds.video.id AS videoId, SUM(vds.viewCount) AS totalViews " +
            "FROM video_daily_statistics vds " +
            "JOIN vds.video v " +
            "WHERE vds.date BETWEEN :startDate AND :endDate " +
            "AND v.creatorId = :userId " +
            "GROUP BY vds.video.id " +
            "ORDER BY totalViews DESC LIMIT 5")
    List<Object[]> findPeriodViewTop5(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT vds FROM video_daily_statistics vds " +
            "JOIN vds.video v " +
            "WHERE vds.date = :today AND v.creatorId = :userId " +
            "ORDER BY vds.playTime DESC LIMIT 5")
    List<VideoDailyStatisticsEntity> findTimeTop5(@Param("userId") String userId, @Param("today") LocalDate today);

    @Query("SELECT vds.video.id AS videoId, SUM(vds.playTime) AS totalTime " +
            "FROM video_daily_statistics vds " +
            "JOIN vds.video v " +
            "WHERE vds.date BETWEEN :startDate AND :endDate " +
            "AND v.creatorId = :userId " +
            "GROUP BY vds.video.id " +
            "ORDER BY totalTime DESC LIMIT 5")
    List<Object[]> findPeriodTimeTop5(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
