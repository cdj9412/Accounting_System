package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDate date;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;
}
