package com.sparta.entity.settlement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "video_monthly_settlement")
@Table(name = "video_monthly_settlement")
public class VideoMonthlySettlementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "month_start_date", nullable = false)
    private LocalDate monthStartDate;

    @Column(name = "month_end_date", nullable = false)
    private LocalDate monthEndDate;

    @Column(name = "video_settlement_amount", nullable = false)
    private Long videoSettlementAmount = 0L;

    @Column(name = "ad_settlement_amount", nullable = false)
    private Long adSettlementAmount = 0L;
}
