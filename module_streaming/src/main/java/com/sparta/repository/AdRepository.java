package com.sparta.repository;

import com.sparta.entity.AdEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRepository extends JpaRepository<AdEntity, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE ad a SET a.totalViews = a.totalViews + 1 WHERE a.id = :adId")
    void incrementTotalViews(@Param("adId") Long adId);
}
