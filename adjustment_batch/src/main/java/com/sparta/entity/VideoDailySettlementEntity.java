package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "video_daily_settlement")
@Table(name = "video_daily_settlement")
public class VideoDailySettlementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "video_settlement_amount", nullable = false)
    private Long videoSettlementAmount = 0L;

    @Column(name = "ad_settlement_amount", nullable = false)
    private Long adSettlementAmount = 0L;
}
