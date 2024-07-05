package com.sparta.repository.settlement;

import com.sparta.entity.settlement.VideoDailySettlementEntity;
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
public interface VideoDailySettlementRepository extends JpaRepository<VideoDailySettlementEntity, Long> {

    @Query("SELECT v FROM video_daily_settlement v WHERE v.videoId = :videoId AND v.date = :today")
    List<VideoDailySettlementEntity> findDailyData(@Param("videoId") Long videoId, @Param("today") LocalDate today);


    @Query("SELECT vds.videoId AS videoId, " +
            "SUM(vds.videoSettlementAmount) AS total_video_settlement_amount, " +
            "SUM(vds.adSettlementAmount) AS total_ad_settlement_amount " +
            "FROM video_daily_settlement vds " +
            "WHERE vds.videoId = :videoId AND" +
            "(vds.date BETWEEN :startDate AND :endDate) " +
            "GROUP BY vds.videoId")
    List<Object[]> findPeriodData(@Param("videoId") Long videoId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
