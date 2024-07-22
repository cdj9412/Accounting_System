package com.sparta.adjustment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity(name = "video_daily_views")
@Table(name = "video_daily_views")
public class VideoDailyViewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long videoId;

    private LocalDate date;

    private Long viewCount;

    private Long watchTime;

    @Builder
    public VideoDailyViewsEntity(Long videoId, LocalDate date, Long viewCount, Long watchTime) {
        this.videoId = videoId;
        this.date = date;
        this.viewCount = viewCount;
        this.watchTime = watchTime;
    }
}
