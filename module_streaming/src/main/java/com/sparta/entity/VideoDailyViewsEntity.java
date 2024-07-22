package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 영상의 일일 시청 수를 나타냄
 * 'video_daily_views' 테이블과 매핑
 */
@Getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="video_daily_views")
@Table(name="video_daily_views")
public class VideoDailyViewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "ad_count")
    private Long adCount;

    @Column(name = "watch_time")
    private Long watchTime;

    public VideoDailyViewsEntity(Long videoId, String userId, LocalDate date, Long adCount, Long watchTime) {
        this.videoId = videoId;
        this.userId = userId;
        this.date = date;
        this.adCount = adCount;
        this.watchTime = watchTime;
    }


    public void incrementDailyWatchTime(Long watchTime) {
        this.watchTime += watchTime;
    }
}
