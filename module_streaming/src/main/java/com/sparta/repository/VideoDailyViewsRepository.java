package com.sparta.repository;

import com.sparta.entity.VideoDailyViewsEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VideoDailyViewsRepository extends JpaRepository<VideoDailyViewsEntity, Long> {
    Optional<VideoDailyViewsEntity> findByVideoIdAndDate(Long videoId, LocalDate date);

    @Transactional
    @Modifying
    @Query("UPDATE video_daily_views v SET v.viewCount = v.viewCount + 1 WHERE v.videoId = :videoId AND v.date =: today")
    void incrementViewCount(Long videoId, LocalDate today);
}
