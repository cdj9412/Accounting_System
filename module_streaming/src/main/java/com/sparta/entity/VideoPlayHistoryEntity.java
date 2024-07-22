package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 동영상 재생 이력을 나타냄
 * 'video_play_history' 테이블과 매핑.
 */
@Getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="video_play_history")
@Table(name="video_play_history")
public class VideoPlayHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "last_play_time")
    private Timestamp lastPlayTime = new Timestamp(System.currentTimeMillis());

    @Column(name = "current_position")
    private int currentPosition = 0;

    @ManyToOne
    @JoinColumn(name = "video_id", insertable = false, updatable = false)
    private VideoEntity video;

    public VideoPlayHistoryEntity(Long videoId, String userId, Timestamp lastPlayTime, int currentPosition) {
        this.videoId = videoId;
        this.userId = userId;
        this.lastPlayTime = lastPlayTime;
        this.currentPosition = currentPosition;
    }
}
