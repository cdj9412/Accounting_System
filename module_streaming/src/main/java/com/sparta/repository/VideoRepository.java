package com.sparta.repository;

import com.sparta.entity.VideoEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    Optional<VideoEntity> findById(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE video v SET v.totalViews = v.totalViews + 1 WHERE v.id = :videoId")
    void incrementTotalViews(@Param("videoId") Long videoId);

}
