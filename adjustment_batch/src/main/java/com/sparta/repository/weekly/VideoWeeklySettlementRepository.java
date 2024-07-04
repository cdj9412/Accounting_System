package com.sparta.repository.weekly;

import com.sparta.entity.weekly.VideoWeeklySettlementEntity;
import com.sparta.entity.weekly.VideoWeeklyStatisticsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoWeeklySettlementRepository extends JpaRepository<VideoWeeklySettlementEntity, Long> {

    @Query("SELECT v FROM video_weekly_settlement v WHERE v.videoId = :videoId AND v.weekStartDate = :monday AND v.weekEndDate = :sunday")
    Optional<VideoWeeklySettlementEntity> findByDate(@Param("videoId") Long videoId, @Param("monday") LocalDate monday, @Param("sunday") LocalDate sunday);

    @Modifying
    @Transactional
    @Query("UPDATE video_weekly_settlement v SET v.videoSettlementAmount = :videoSettlementAmount, v.adSettlementAmount = :adSettlementAmount " +
            "WHERE v.videoId = :videoId AND v.weekStartDate = :monday AND v.weekEndDate = :sunday")
    void updateWeeklyStatistics(@Param("videoId") Long videoId,
                                @Param("monday") LocalDate monday, @Param("sunday") LocalDate sunday,
                                @Param("videoSettlementAmount") Long videoSettlementAmount, @Param("adSettlementAmount") Long adSettlementAmount);
}
