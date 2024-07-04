package com.sparta.entity.settlement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "video_weekly_settlement")
@Table(name = "video_weekly_settlement")
public class VideoWeeklySettlementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "week_end_date", nullable = false)
    private LocalDate weekEndDate;

    @Column(name = "video_settlement_amount", nullable = false)
    private Long videoSettlementAmount = 0L;

    @Column(name = "ad_settlement_amount", nullable = false)
    private Long adSettlementAmount = 0L;
}
