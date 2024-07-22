package com.sparta.repository;

import com.sparta.entity.VideoAdEntity;
import com.sparta.entity.VideoAdId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoAdRepository extends JpaRepository<VideoAdEntity, VideoAdId> {

    @Query("SELECT v FROM video_ad v WHERE v.id.videoId = :videoId")
    List<VideoAdEntity> findByVideoId(@Param("videoId") Long videoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM video_ad v WHERE v.id.videoId = :videoId AND v.id.adId = :adId")
    VideoAdEntity findByVideoIdAndAdIdWithPessimisticLock(@Param("videoId") Long videoId, @Param("adId") Long adId);


}
