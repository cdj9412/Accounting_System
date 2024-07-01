package com.sparta.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 복합 키를 나타내는 클래스
 * VideoAd 엔티티에서 사용
 */
@Getter
public class VideoAdId implements Serializable {
    private Long videoId;
    private Long adId;

    // 기본 생성자
    public VideoAdId() {}

    // 파라미터를 받는 생성자
    public VideoAdId(Long videoId, Long adId) {
        this.videoId = videoId;
        this.adId = adId;
    }

    // Getters, setters, equals, and hashCode methods
    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoAdId that = (VideoAdId) o;
        return Objects.equals(videoId, that.videoId) && Objects.equals(adId, that.adId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, adId);
    }
}
