package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 영상의 일일 광고 시청 수를 나타냄
 * 'video_ad_daily_views' 테이블과 매핑
 */
@Getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="video_ad_daily_views")
@Table(name="video_ad_daily_views")
public class VideoAdDailyViewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "ad_id", nullable = false)
    private Long adId;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;
}
