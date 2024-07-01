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

    @Query("SELECT v.lastPlayTime FROM video_play_history v WHERE v.videoId = :videoId AND v.memberId = :memberId")
    Timestamp findLastPlayTimeByVideoIdAndMemberId(@Param("videoId") Long videoId, @Param("memberId") String memberId);

    Optional<VideoPlayHistoryEntity> findByVideoIdAndMemberId(Long videoId, String memberId);

    @Transactional
    @Modifying
    @Query("UPDATE video_play_history v SET v.lastPlayTime = :currentTime WHERE v.videoId = :videoId")
    void updateLastPlayTime(@Param("videoId") Long videoId, @Param("currentTime") Timestamp currentTime);


    @Transactional
    @Modifying
    @Query("UPDATE video_play_history v SET v.currentPosition = :stopPoint WHERE v.videoId = :videoId AND v.memberId = :memberId")
    void updateCurrentPosition(@Param("videoId") Long videoId, @Param("memberId") String memberId, @Param("stopPoint") int stopPoint);
}
