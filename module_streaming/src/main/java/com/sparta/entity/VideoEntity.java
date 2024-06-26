package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 'video' 테이블과 매핑
 */
@Getter // 멤버 getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="video")
@Table(name="video")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id", length = 255, nullable = false)
    private String creatorId;

    @Column(name = "total_views", nullable = false)
    private Long totalViews = 0L;

    @Column(name = "running_time", nullable = false)
    private Long runningTime = 0L;

    // VideoPlayHistory 와 일대다 관계
    @OneToMany(mappedBy = "video")
    private Set<VideoPlayHistoryEntity> playHistories;

    // VideoAd 와 일대다 관계
    @OneToMany(mappedBy = "video")
    private Set<VideoAdEntity> videoAds;


}
