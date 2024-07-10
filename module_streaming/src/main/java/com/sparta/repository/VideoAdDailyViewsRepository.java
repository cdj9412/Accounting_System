package com.sparta.repository;

import com.sparta.entity.VideoAdDailyViewsEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoAdDailyViewsRepository extends JpaRepository<VideoAdDailyViewsEntity, Long> {

    Optional<VideoAdDailyViewsEntity> findByVideoIdAndAdIdAndDate(Long videoId, Long adId, LocalDate today);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM video_ad_daily_views v WHERE v.videoId = :videoId AND v.adId = :adId AND v.date = :date")
    VideoAdDailyViewsEntity findByVideoIdAdIdAndDateWithPessimisticLock(@Param("videoId") Long videoId, @Param("adId") Long adId, @Param("date") LocalDate date);

}
