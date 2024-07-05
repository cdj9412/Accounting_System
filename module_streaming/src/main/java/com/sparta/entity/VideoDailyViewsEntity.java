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

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "watch_time", nullable = false)
    private Long watchTime;

    public VideoDailyViewsEntity(Long videoId, LocalDate date, Long viewCount, Long watchTime) {
        this.videoId = videoId;
        this.date = date;
        this.viewCount = viewCount;
        this.watchTime = watchTime;
    }
}
