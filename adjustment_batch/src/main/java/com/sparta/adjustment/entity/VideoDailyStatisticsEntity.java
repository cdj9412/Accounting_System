package com.sparta.adjustment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "video_daily_statistics")
@Table(name = "video_daily_statistics")
public class VideoDailyStatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long videoId;

    private LocalDate date;

    private Long viewCount = 0L;

    private Long adViewCount = 0L;

    private Long playTime = 0L;

    @Builder
    public VideoDailyStatisticsEntity(Long videoId, LocalDate date, Long viewCount, Long adViewCount, Long playTime) {
        this.videoId = videoId;
        this.date = date;
        this.viewCount = viewCount;
        this.adViewCount = adViewCount;
        this.playTime = playTime;
    }

}
