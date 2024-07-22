package com.sparta.adjustment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity(name = "video_daily_settlement")
@Table(name = "video_daily_settlement")
public class VideoDailySettlementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long videoId;

    private LocalDate date;

    private Long videoSettlementAmount = 0L;

    private Long adSettlementAmount = 0L;

    @Builder
    public VideoDailySettlementEntity(Long videoId, LocalDate date, Long videoSettlementAmount, Long adSettlementAmount) {
        this.videoId = videoId;
        this.date = date;
        this.videoSettlementAmount = videoSettlementAmount;
        this.adSettlementAmount = adSettlementAmount;
    }

}
