package com.sparta.repository;

import com.sparta.entity.VideoDailyViewsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoDailyViewsRepository extends JpaRepository<VideoDailyViewsEntity, Long> {
    Optional<VideoDailyViewsEntity> findByVideoIdAndDate(Long videoId, LocalDate date);

    @Transactional
    @Modifying
    @Query("UPDATE video_daily_views v SET v.viewCount = v.viewCount + 1 WHERE v.videoId = :videoId AND v.date = :date")
    void incrementViewCount(@Param("videoId") Long videoId, @Param("date") LocalDate date);


    @Transactional
    @Modifying
    @Query("UPDATE video_daily_views v SET v.watchTime = v.watchTime + :watchTime WHERE v.videoId = :videoId AND v.date = :date")
    void incrementWatchTime(@Param("videoId") Long videoId, @Param("date") LocalDate date , @Param("watchTime") Long watchTime);
}
