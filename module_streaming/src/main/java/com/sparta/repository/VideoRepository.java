package com.sparta.repository;

import com.sparta.entity.VideoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    Optional<VideoEntity> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM video v WHERE v.id = :videoId")
    Optional<VideoEntity> findByIdWithPessimisticLock(@Param("videoId") Long videoId);

}
