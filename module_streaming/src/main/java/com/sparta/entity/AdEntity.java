package com.sparta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 광고를 나타냄.
 * 'ad' 테이블과 매핑
 */
@Getter
@NoArgsConstructor // 매개변수없는 생성자 자동 생성
@AllArgsConstructor // 모든 필드에 대한 생성자 생성
@Entity(name="ad")
@Table(name="ad")
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category")
    private String category;

    // VideoAd 와 일대다 관계
    @OneToMany(mappedBy = "ad")
    private Set<VideoAdEntity> videoAds;

}
