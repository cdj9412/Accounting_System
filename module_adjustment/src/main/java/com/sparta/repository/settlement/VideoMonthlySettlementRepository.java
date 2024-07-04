package com.sparta.repository.settlement;

import com.sparta.entity.settlement.VideoMonthlySettlementEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoMonthlySettlementRepository extends JpaRepository<VideoMonthlySettlementEntity, Long> {

    @Query("SELECT v FROM video_monthly_settlement v WHERE v.videoId = :videoId AND v.monthEndDate = :startDate AND v.monthEndDate = :endDate")
    Optional<VideoMonthlySettlementEntity> findByDate(@Param("videoId") Long videoId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Modifying
    @Transactional
    @Query("UPDATE video_monthly_settlement v SET v.videoSettlementAmount = :videoSettlementAmount, v.adSettlementAmount = :adSettlementAmount " +
            "WHERE v.videoId = :videoId AND v.monthStartDate = :startDate AND v.monthEndDate = :endDate")
    void updateMonthlyStatistics(@Param("videoId") Long videoId,
                                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                @Param("videoSettlementAmount") Long videoSettlementAmount, @Param("adSettlementAmount") Long adSettlementAmount);

}
