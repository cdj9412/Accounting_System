package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "video")
@Table(name = "video")
public class VideoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "total_views", nullable = false)
    private Long totalViews;

    @Column(name = "running_time", nullable = false)
    private Long runningTime;
}
