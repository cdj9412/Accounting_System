package com.sparta.repository;

import com.sparta.entity.VideoPlayHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface VideoHistoryRepository extends JpaRepository<VideoPlayHistoryEntity, Long> {

    @Query("SELECT v.lastPlayTime FROM video_play_history v WHERE v.videoId = :videoId AND v.userId = :userId")
    Timestamp findLastPlayTimeByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") String userId);

    Optional<VideoPlayHistoryEntity> findByVideoIdAndUserId(Long videoId, String userId);

    @Transactional
    @Modifying
    @Query("UPDATE video_play_history v " +
            "SET v.lastPlayTime = :currentTime " +
            "WHERE v.videoId = :videoId " +
            "AND v.id = (SELECT MAX(id) FROM video_play_history WHERE videoId = :videoId)")
    int updateLastPlayTime(@Param("videoId") Long videoId, @Param("currentTime") Timestamp currentTime);

    @Transactional
    @Modifying
    @Query("UPDATE video_play_history v SET v.currentPosition = :stopPoint WHERE v.videoId = :videoId AND v.userId = :userId")
    void updateCurrentPosition(@Param("videoId") Long videoId, @Param("userId") String userId, @Param("stopPoint") int stopPoint);
}
