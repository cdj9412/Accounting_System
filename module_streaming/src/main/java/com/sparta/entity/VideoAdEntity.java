package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 동영상과 광고의 연결을 나타내는 엔티티
 * 'video_ad' 테이블과 매핑
 */
@Getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="video_ad")
@Table(name="video_ad")
public class VideoAdEntity {
    @EmbeddedId
    private VideoAdId id;

    @ManyToOne
    @MapsId("videoId")
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    @ManyToOne
    @MapsId("adId")
    @JoinColumn(name = "ad_id")
    private AdEntity ad;

    @Column(name = "ad_position")
    private int adPosition;

    @Column(name = "ad_views")
    private Long adViews = 0L;

    // adViews 를 증가시키는 메서드
    public void incrementAdViews() {
        this.adViews++;
    }

}
