package com.sparta.adjustment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity(name="video_ad_daily_views")
@Table(name="video_ad_daily_views")
public class VideoAdDailyViewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long videoId;

    private Long adId;

    private LocalDate date;

    private Long viewCount;

    @Builder
    public VideoAdDailyViewsEntity(Long videoId, Long adId, LocalDate date, Long viewCount) {
        this.videoId = videoId;
        this.adId = adId;
        this.date = date;
        this.viewCount = viewCount;
    }
}
