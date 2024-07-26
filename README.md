# 정산 시스템 프로젝트(Accounting_System)

📅 **2024년 6월 ~ 2024년 7월**

### ⚙️️ 주요 기술 스택

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-4.1.4-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Cloud Eureka](https://img.shields.io/badge/Spring%20Cloud%20Eureka-4.1.2-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.8-02303A?style=flat-square&logo=gradle&logoColor=white)

## 📌 프로젝트 소개

- 대량의 영상 시청기록에 대한 통계 및 정산 Batch 작업
- 부하분산을 위한 MSA 구조

## 🛠️ 기능
1. User Service
   <table>
     <tr>
       <th>기능 구분</th>
       <th>기능</th>
     </tr>
     <tr>
       <td rowspan="3">이용자 관리</td>
       <td>회원가입</td>
     </tr>
     <tr>
       <td>로그인</td>
     </tr>
     <tr>
       <td>로그아웃</td>
     </tr>
   </table>

2. Streaming Service
   <table>
     <tr>
       <th>기능 구분</th>
       <th>기능</th>
     </tr>
     <tr>
       <td rowspan="3">동영상 재생 관리</td>
       <td>동영상 재생</td>
     </tr>
     <tr>
       <td>동영상 중단</td>
     </tr>
     <tr>
       <td>동영상 재생 관리</td>
     </tr>
   </table>

3. Adjustment Service
   <table>
     <tr>
       <th>기능 구분</th>
       <th>기능</th>
     </tr>
     <tr>
       <td rowspan="2">동영상 통계 조회</td>
       <td>일간/주간/월간 조회 수 Top 5</td>
     </tr>
     <tr>
       <td>일간/주간/월간 재생 시간 Top 5</td>
     </tr>
     <tr>
       <td rowspan="1">동영상 정산 조회</td>
       <td>일간/주간/월간 정산 데이터 조회</td>
     </tr>
   </table>


## 🔥 프로젝트 목표
1. 1억 건의 데이터에 대한 배치 작업을 2분대로 처리
2. 멀티 스레드와 멀티 프로세스 환경에서 원활하게 서비스 동작

## 🔍 아키텍처
![아키텍쳐](https://github.com/user-attachments/assets/3bc52d73-0f1f-4a58-941f-83889750dad0)


## 📃 프로젝트 상세

### 📘 API 문서

문서 수정중... 차후 업데이트

### ERD

![image](https://github.com/user-attachments/assets/09012cda-f299-4dd5-9316-81b2b6478285)


## 🏷️ 프로젝트 주요 경험

### 1. 배치 작업 성능 개선 (97% 향상)
[📚 성능 테스트 과정 상세](https://choidj94.notion.site/Spring-Batch-aaca97f9203f4351baa60ef791f1a43b?pvs=4)

<details>
<summary><strong>Quick Overview</strong></summary>

#### 📊 최종 성능
**1억 건 기준 실측 결과: 1m 20sec 340ms**

#### 📈 성능 개선 추이

| 단계 | 데이터 규모 | 처리 시간 | 개선율 |
|------|------------|-----------|--------|
| 최적화 전 | 1억 건 | 60분+ | - |
| 1차 최적화 | 1억 건 | 21분(통계) + 30분+(정산) | 14%+ ↓ |
| 2차 최적화 | 1억 건 | 1분 20초 | 97% ↓ |

*1차 최적화 결과의 정산 처리 시간은 약 1000만 건 기준 실측치(3분 13초)를 바탕으로 1억 건에 대한 처리시간을 추정한 값입니다.

#### 🚀 최적화별 주요 개선 내역
1. **1차 최적화**: JPA 제거, JDBC 직접 사용, 벌크 연산 적용, 데이터베이스 인덱싱
2. **2차 최적화**: DB구조 변경 및 쿼리 최적화, Spring Batch 파티셔닝 도입, DB 파티션 프루닝 적용, Chunk 크기 최적화 

</details>

### 2. 통계 및 정산 최적화
- 플랫폼 스레드 활용
- Chunk 동시성 제어
- Spring Batch/DB Partitioning

### 3. 부하 분산 및 서비스 매핑

<strong>3.1 Spring Cloud Gateway</strong>

- 중앙 집중식 인증 및 권한 부여, JWT 토큰 검증
- 로드 밸런싱: 라운드 로빈 방식으로 스트리밍 서비스 트래픽 분산

<strong>3.2 Spring Cloud Eureka</strong>

- Eureka 서비스 ID를 활용한 자동 서비스 매핑
   - Eureka에 등록된 서비스 ID를 활용하여 요청을 자동으로 해당 서비스로 매핑
   - 멀티 프로세스 환경에서 API 이벤트의 효율적인 부하 분산
- Eureka Server를 통한 서비스 디스커버리
   - 서비스 자동 등록 및 검색
   - 서비스 헬스 체크 및 실시간 상태 모니터링


<strong>3.3 Streaming Service CQRS</strong>

- CQRS 패턴 적용
   - 쓰기 작업과 읽기 작업 분리
- DB Main-Replica 구조 구현
   | 구분 | 역할 | 특징 |
   |------|------------|-----------|
   | Master DB| 쓰기 작업 | 데이터 일관성 보장 |
   | Slave DB | 읽기 작업 | 조회 성능 최적화 |
   - DB 간 ROW단위 실시간 동기화로 데이터 정합성 유지
- 트래픽 분산 및 가용성 향상
   - 읽기 작업의 부하를 Slave DB로 분산


### 4. 트러블 슈팅
- [API Gateway 인증 처리 불가 문제](https://choidj94.notion.site/API-Gateway-784aece52e2b4f12a2ae534e7499d16b?pvs=4)
- [MySQL replication Master-Slave 연결 해제 문제](https://choidj94.notion.site/MySQL-replication-Master-Slave-e91e1d634e6f41ce918278276ed72f6d?pvs=4)
  
