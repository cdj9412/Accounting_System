package com.sparta.service.impl;

import com.sparta.dto.request.CompleteRequestDto;
import com.sparta.dto.request.PlayRequestDto;
import com.sparta.dto.request.StopRequestDto;
import com.sparta.dto.response.CompleteResponseDto;
import com.sparta.dto.response.PlayResponseDto;
import com.sparta.dto.response.StopEnum;
import com.sparta.dto.response.StopResponseDto;
import com.sparta.entity.VideoAdEntity;
import com.sparta.entity.VideoDailyViewsEntity;
import com.sparta.entity.VideoEntity;
import com.sparta.entity.VideoPlayHistoryEntity;
import com.sparta.repository.*;
import com.sparta.service.StreamingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StreamingServiceImplement implements StreamingService {
    private final VideoRepository videoRepository;
    private final VideoHistoryRepository videoHistoryRepository;
    private final VideoDailyViewsRepository videoDailyViewsRepository;
    private final VideoAdRepository videoAdRepository;

    // 동영상 재생
    // 영상관련 조회수 증가 용도
    @Transactional
    @Override
    public PlayResponseDto play(PlayRequestDto playRequestDto) {
        Long videoId = playRequestDto.getVideoId();
        String userId = playRequestDto.getUserId();
        int startPoint = 0;
        // 어뷰징 체크
        if (abusingCheck(videoId, userId))
            return new PlayResponseDto(userId, videoId, startPoint, true);

        // 어뷰징 체크 통과 시
        // 재생 기록 남기기
        playHistoryUpdate(videoId, userId);

        // 중간 재생지점 가져오기
        startPoint = middlePlayCheck(videoId, userId);

        // 중간 재생지점이 영상의 총 길이와 같으면 startPoint 0으로 초기화
        VideoEntity videoInfo = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        if (startPoint == videoInfo.getRunningTime())
            startPoint = 0;

        // 중간지점 재생이어도 조회수 올리기
        // 일일 조회수(dailyViews), 총 조회수(video)
        incrementTotalViews(videoId);

        return new PlayResponseDto(userId, videoId, startPoint, false);
    }

    // 어뷰징 체크
    private boolean abusingCheck(Long videoId, String userId) {
        // 1. video 게시자가 시청자 일 경우
        VideoEntity videoInfo = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));
        if (userId.equals(videoInfo.getCreatorId()))
            return true;

        // 2. 기존 시청기록이 30초 이내에 있을 경우
        Timestamp timestamp = videoHistoryRepository.findLastPlayTimeByVideoIdAndUserId(videoId, userId);
        if (timestamp != null) {
            Instant now = Instant.now();
            Instant lastPlayTime = timestamp.toInstant();
            Duration duration = Duration.between(lastPlayTime, now);
            // 30초 이내이면 true 반환
            return duration.getSeconds() <= 30;
        }
        else
            return false;
    }

    // 재생기록 작성
    private void playHistoryUpdate(Long videoId, String userId) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        // 쿼리 방식 변경 - 2024/07/09
        int updateRow = videoHistoryRepository.updateLastPlayTime(videoId, currentTime);
        if (updateRow == 0) {
            // 재생 기록이 존재하지 않는 경우: 새로운 재생 기록 생성
            VideoPlayHistoryEntity newPlayHistory = new VideoPlayHistoryEntity(videoId, userId, currentTime, 0);
            videoHistoryRepository.save(newPlayHistory); // 새로운 엔티티 저장
        }
    }

    // 중간 재생 체크
    private int middlePlayCheck(Long videoId, String userId) {
        VideoPlayHistoryEntity playHistory = videoHistoryRepository.findByVideoIdAndUserId(videoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Video play history not found"));
        return playHistory.getCurrentPosition();
    }

    // Pessimistic lock 사용을 위한 코드 수정 및 추가
    @Transactional
    protected void incrementTotalViews(Long videoId) {
        // 비관적 락을 사용해 비디오 엔티티 조회
        VideoEntity video = videoRepository.findByIdWithPessimisticLock(videoId)
                .orElseThrow(() -> new EntityNotFoundException("Video not found"));

        // 조회한 비디오 엔티티의 totalViews 증가
        video.incrementTotalViews();

        // 변경 사항 저장
        videoRepository.save(video);
    }


    // 동영상 중단
    @Transactional
    @Override
    public StopResponseDto stop(StopRequestDto stopRequestDto) {
        Long videoId = stopRequestDto.getVideoId();
        String userId = stopRequestDto.getUserId();
        int stopPoint = stopRequestDto.getCurrentPosition(); // 중단 시점
        int startPoint = middlePlayCheck(videoId, userId); // 시작 시점

        // 예외 처리 - stopPoint 가 동영상의 runningTime 보다 크면 불가능 리턴
        Optional<VideoEntity> videoEntity = videoRepository.findById(videoId);
        if(videoEntity.isPresent()) {
            int runningTime = videoEntity.get().getRunningTime();
            if(runningTime < stopPoint || startPoint > stopPoint) {
                return new StopResponseDto(userId, videoId, stopPoint, StopEnum.CONTENT);
            }
        }
        else {
            return new StopResponseDto(userId, videoId, stopPoint, StopEnum.DB);
        }

        // 광고 재생 체크 및 조회수 변경
        adViewsUpdate(videoId, userId, startPoint, stopPoint);

        // 시청시간 추가
        addWatchTime(videoId, userId, (long) (stopPoint - startPoint));

        // current_position 변경
        videoHistoryRepository.updateCurrentPosition(videoId, userId, stopPoint);

        return new StopResponseDto(userId, videoId, stopPoint, StopEnum.SUCCESS);
    }

    // 동영상 시청 완료
    @Transactional
    @Override
    public CompleteResponseDto complete(CompleteRequestDto completeRequestDto) {
        Long videoId = completeRequestDto.getVideoId();
        String userId = completeRequestDto.getUserId();
        Optional<VideoEntity> videoEntity = videoRepository.findById(videoId);

        int completePoint;
        if(videoEntity.isPresent()) {
            completePoint = videoEntity.get().getRunningTime();
        }
        else {
            return new CompleteResponseDto(userId, videoId, false);
        }

        // 광고 재생 체크 및 조회수 변경
        adViewsUpdate(videoId, userId, 0, completePoint);

        // 시청시간 추가
        addWatchTime(videoId, userId, (long) completePoint);

        // current_position 변경
        videoHistoryRepository.updateCurrentPosition(videoId, userId, completePoint);

        return new CompleteResponseDto(userId, videoId, true);
    }

    // 광고 재생 체크 및 조회수 변경
    // 카운팅 테이블 : ad, video_ad, video_ad_daily_views
    @Transactional
    protected void adViewsUpdate(Long videoId, String userId, int startPoint, int stopPoint) {
        List<VideoAdEntity> videoAds = videoAdRepository.findByVideoId(videoId);

        for(VideoAdEntity videoAd : videoAds) {
            int adPosition = videoAd.getAdPosition();
            Long adId = videoAd.getId().getAdId();

            // 광고 위치가 startPoint 와 stopPoint 사이에 있는 경우
            if (adPosition >= startPoint && adPosition <= stopPoint) {
                // ad_views 를 1 증가시킴
                incrementAdViews(videoId, adId);

                // ad, video_ad_daily_views 의  view_count 를 1 증가 시킴
                dailyPlayAdViewsUpdate(videoId, userId, (long)(stopPoint-startPoint));
            }
            else {
                dailyPlayAdViewsUpdate(videoId, userId, 0L);
            }
        }
    }

    @Transactional
    protected void incrementAdViews(Long videoId,Long adId) {
        // 비관적 락을 사용하여 엔티티를 조회
        VideoAdEntity videoAdEntity = videoAdRepository.findByVideoIdAndAdIdWithPessimisticLock(videoId, adId);

        if (videoAdEntity != null) {
            // 조회된 엔티티의 adViews 필드를 업데이트
            videoAdEntity.incrementAdViews();
            videoAdRepository.save(videoAdEntity); // 엔티티 저장
        }
    }

    // 일일 광고 조회수 내역 체크 및 조회수 증가
    @Transactional
    protected void dailyPlayAdViewsUpdate(Long videoId, String userId, Long watchTime) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        VideoDailyViewsEntity newEntity;
        if(watchTime == 0)
            newEntity = new VideoDailyViewsEntity(videoId, userId, today, 1L, watchTime);
        else
            newEntity = new VideoDailyViewsEntity(videoId, userId, today, 0L, watchTime);

        videoDailyViewsRepository.save(newEntity); // 저장하여 새로운 엔티티 생성

    }

    // 시청시간 추가
    @Transactional
    protected void addWatchTime(Long videoId, String userId,  Long watchTime) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        VideoDailyViewsEntity entity = videoDailyViewsRepository.findByVideoIdAndDateWithPessimisticLock(videoId, userId, today);
        if (entity != null) {
            entity.incrementDailyWatchTime(watchTime);
            videoDailyViewsRepository.save(entity);
        }
    }

}
