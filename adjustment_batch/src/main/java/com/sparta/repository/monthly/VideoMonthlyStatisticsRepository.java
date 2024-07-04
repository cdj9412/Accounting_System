package com.sparta.repository.monthly;

import com.sparta.entity.monthly.VideoMonthlyStatisticsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoMonthlyStatisticsRepository extends JpaRepository<VideoMonthlyStatisticsEntity, Long> {

    @Query("SELECT v FROM video_monthly_statistics v WHERE v.videoId = :videoId AND v.monthEndDate = :startDate AND v.monthEndDate = :endDate")
    Optional<VideoMonthlyStatisticsEntity> findByDate(@Param("videoId") Long videoId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Modifying
    @Transactional
    @Query("UPDATE video_monthly_statistics v SET v.viewCount = :viewCount, v.playTime = :playTime " +
            "WHERE v.videoId = :videoId AND v.monthStartDate = :startDate AND v.monthEndDate = :endDate")
    void updateMonthlyStatistics(@Param("videoId") Long videoId,
                                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                @Param("viewCount") Long viewCount, @Param("playTime") Long playTime);

}
