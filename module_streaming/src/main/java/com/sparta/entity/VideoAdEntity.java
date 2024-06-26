package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * 동영상과 광고의 연결을 나타내는 엔티티
 * 'video_ad' 테이블과 매핑
 */
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="video_ad")
@Table(name="video_ad")
public class VideoAdEntity {
    @Id
    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Id
    @Column(name = "ad_id", nullable = false)
    private Long adId;

    @Column(name = "ad_position", nullable = false)
    private Long adPosition;

    @Column(name = "ad_views", nullable = false)
    private Long adViews = 0L;

    @ManyToOne
    @JoinColumn(name = "video_id", insertable = false, updatable = false)
    private VideoEntity video;

    @ManyToOne
    @JoinColumn(name = "ad_id", insertable = false, updatable = false)
    private AdEntity ad;
}
