package com.sparta.repository;

import com.sparta.entity.VideoDailySettlementEntity;
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

    Optional<VideoDailySettlementEntity> findByVideoIdAndDate(Long videoId, LocalDate date);

    @Modifying
    @Transactional
    @Query("UPDATE video_daily_settlement v SET v.videoSettlementAmount = :videoSettlementAmount, v.adSettlementAmount = :adSettlementAmount WHERE v.videoId = :videoId AND v.date = :date")
    void updateDailySettlement(@Param("videoId") Long videoId, @Param("date") LocalDate date, @Param("videoSettlementAmount") Long videoSettlementAmount, @Param("adSettlementAmount") Long adSettlementAmount);

    @Modifying
    @Transactional
    @Query("UPDATE video_daily_settlement v SET v.adSettlementAmount = :adSettlementAmount WHERE v.videoId = :videoId AND v.date = :date")
    void updateAdSettlementAmount(@Param("videoId") Long videoId, @Param("date") LocalDate date, @Param("adSettlementAmount") Long adSettlementAmount);

}
