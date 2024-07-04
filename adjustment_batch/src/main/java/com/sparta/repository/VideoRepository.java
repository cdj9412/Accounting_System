package com.sparta.repository;

import com.sparta.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    Optional<VideoEntity> findById(Long id);
}
