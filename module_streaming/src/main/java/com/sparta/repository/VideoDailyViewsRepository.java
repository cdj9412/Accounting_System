package com.sparta.repository;

import com.sparta.entity.VideoDailyViewsEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoDailyViewsRepository extends JpaRepository<VideoDailyViewsEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM video_daily_views v WHERE v.videoId = :videoId AND v.userId = :userId AND v.date = :date")
    VideoDailyViewsEntity findByVideoIdAndDateWithPessimisticLock(@Param("videoId") Long videoId, @Param("userId") String userId, @Param("date") LocalDate date);

}
