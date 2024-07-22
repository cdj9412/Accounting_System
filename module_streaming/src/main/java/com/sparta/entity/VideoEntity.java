package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 'video' 테이블과 매핑
 */
@Getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="video")
@Table(name="video")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "total_views")
    private Long totalViews = 0L;

    @Column(name = "running_time")
    private int runningTime = 0;

    // VideoPlayHistory 와 일대다 관계
    @OneToMany(mappedBy = "video")
    private Set<VideoPlayHistoryEntity> playHistories;

    // VideoAd 와 일대다 관계
    @OneToMany(mappedBy = "video")
    private Set<VideoAdEntity> videoAds;

    // totalViews 를 증가시키는 메서드
    public void incrementTotalViews() {
        this.totalViews++;
    }

}
