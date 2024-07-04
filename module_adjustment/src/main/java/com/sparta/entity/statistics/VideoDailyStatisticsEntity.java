package com.sparta.entity.statistics;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "play_time", nullable = false)
    private Long playTime = 0L;

}
