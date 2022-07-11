# 트리플여행자 클럽 마일리지 서비스 구현

## 개요
트리플 사용자들이 장소에 리뷰를 작성할 때 포인트를 부여하고, 전체/개인에 대한 포인트 부여 히스토리와 개인별 누적 포인트를 관리하기 위한 서비스를 구현

## 요구사항
- 한 사용자는 장소마다 리뷰를 1개만 작성할 수 있고, 리뷰는 수정 또는 삭제 가능.
  - 내용 점수
    - 1자 이상 텍스트 작성: 1점
    - 1장 이상 사진 첨부: 1점
  - 보너스 점수
    - 특정 장소에 첫 리뷰 작성: 1점
- 포인트 증감이 있을 때마다 이력이 남아야 함.
- 사용자마다 현재 시점의 포인트 총점을 조회하거나 계산할 수 있어야 함.
- 포인트 부여 API 구현에 필요한 SQL 수행 시, 전체 테이블 스캔이 일어나지 않는 인덱스가 필요함.
- 리뷰를 작성했다가 삭제하면 해당 리뷰로 부여한 내용 점수와 보너스 점수는 회수함.
- 리뷰를 수정하면 수정한 내용에 맞는 내용 점수를 계산하여 점수를 부여하거나 회수함.
  - 글만 작성한 리뷰에 사진을 추가하면 1점을 부여함.
  - 글과 사진이 있는 리뷰에서 사진을 모두 삭제하면 1점을 회수함.
- 사용자 입장에 본 '첫 리뷰'일 경우 보너스 점수를 부여함.
  - 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하고, 삭제된 이후 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여함.
  - 어떤 장소에 사용자 A가 리뷰를 남겼다가 삭제하는데, 삭제되기 이전 사용자 B가 리뷰를 남기면 사용자 B에게 보너스 점수를 부여하지 않음.
  

## 실행 방법
```text
# Git Repository Clone
git clone https://github.com/oke2103/triple_hw.git

# Application Build
{projectPath}/gradlew clean build 

# Start Application with Database
java -jar {projectPath}/build/libs/hw-1.0.jar 
     -Dspring.datasource.url={database_url}
     -Dspring.datasource.username={database_username}
     -Dspring.datasource.password={database_password}

# 하단의 DDL Scripts 실행     
```

## API 호출 양식
POST / events

```json
{
    "type": "REVIEW",
    "action": "ADD",
    "reviewId": "9bf2ad3d-8ccc-4346-956c-39ccd0d9bca1",
    "content": "좋아요",
    "attachedPhotoIds": [
        "6d6c53f1-bd69-4125-80a1-60426ceddcc3",
        "69f989c1-7874-49fa-94f8-714a04d52fec"
    ],
    "userId": "85cd3af1-124e-4699-abdd-449b0129961a",
    "placeId": "98fbf902-e805-4b16-9ff8-338e65c23b34"
}
```
- type: String
  - REVIEW : 리뷰 이벤트
- action : String
  - ADD: 리뷰 생성
  - MOD: 리뷰 수정
  - DELETE: 리뷰 삭제
- reviewId: UUID / 리뷰 ID
- attachedPhotoIds: List(UUID) / 첨부파일 목록
- userId: UUID / 유저 ID
- placeId: UUID / 장소 ID

## API 기능 명세
### 리뷰 생성
- Request
  - Method: POST
  - URI: /events
  - Headers
    - Content-Type: application/json
  - Body
```json
{
    "type": "REVIEW",
    "action": "ADD",
    "reviewId": "9bf2ad3d-8ccc-4346-956c-39ccd0d9bca1",
    "content": "좋아요",
    "attachedPhotoIds": [
        "6d6c53f1-bd69-4125-80a1-60426ceddcc3",
        "69f989c1-7874-49fa-94f8-714a04d52fec"
    ],
    "userId": "85cd3af1-124e-4699-abdd-449b0129961a",
    "placeId": "98fbf902-e805-4b16-9ff8-338e65c23b34"
}
```
- Response
  - 신규 등록한 리뷰의 ID 값을 반환.
```json
{
    "reviewId": "9bf2ad3d-8ccc-4346-956c-39ccd0d9bca1"
}
```
- 수행 내역
  - 신규 리뷰 작성
  - 리뷰 작성에 따른 포인트 획득
    - 텍스트 작성 시 1점 획득
    - 첨부파일 작성 시 1점 획득
    - 장소에 대한 첫 리뷰 작성 시 1점 획득
  - 포인트 획득에 대한 이력 추가
  
### 리뷰 수정
- Request
  - Method: POST
  - URI: /events
  - Headers
    - Content-Type: application/json
  - Body
```json
{
    "type": "REVIEW",
    "action": "MOD",
    "reviewId": "9bf2ad3d-8ccc-4346-956c-39ccd0d9bca1",
    "content": "",
    "attachedPhotoIds": [
        "6d6c53f1-bd69-4125-80a1-60426ceddcc3",
        "69f989c1-7874-49fa-94f8-714a04d52fec"
    ],
    "userId": "85cd3af1-124e-4699-abdd-449b0129961a",
    "placeId": "98fbf902-e805-4b16-9ff8-338e65c23b34"
}
```
- Response
  - 수정한 리뷰의 ID 값을 반환.
```json
{
    "reviewId": "9bf2ad3d-8ccc-4346-956c-39ccd0d9bca1"
}
```
- 수행 내역
  - 리뷰 내역 수정
  - 리뷰 수정에 따른 포인트 증감
    - 기존 텍스트 점수가 존재하고 수정된 텍스트가 내용이 없다면 1점 회수
    - 기존 첨부파일 점수가 존재하고 수정된 첨부파일이 없다면 1점 회수
  - 포인트 증감에 대한 이력 추가
  
### 리뷰 삭제
- Request
  - Method: POST
  - URI: /events
  - Headers
    - Content-Type: application/json
  - Body
```json
{
    "type": "REVIEW",
    "action": "DELETE",
    "reviewId": "9bf2ad3d-8ccc-4346-956c-39ccd0d9bca1",
    "content": "",
    "attachedPhotoIds": [
        "6d6c53f1-bd69-4125-80a1-60426ceddcc3",
        "69f989c1-7874-49fa-94f8-714a04d52fec"
    ],
    "userId": "85cd3af1-124e-4699-abdd-449b0129961a",
    "placeId": "98fbf902-e805-4b16-9ff8-338e65c23b34"
}
```
- Response
  - 삭제한 리뷰의 ID 값을 반환.
```json
{
    "reviewId": "9bf2ad3d-8ccc-4346-956c-39ccd0d9bca1"
}
```
- 수행 내역
  - 리뷰 내역 삭제
  - 리뷰 삭제에 따른 포인트 회수
    - 기존 텍스트 점수가 존재하면 1점 회수
    - 기존 첨부파일 점수가 존재하면 1점 회수
    - 기존 첫 리뷰 작성 점수가 존재하면 1점 회수
  - 포인트 회수에 대한 이력 추가

### 포인트 조회
- Request
  - Method: GET
  - URI: /{userId}/point
  
- Response
  - 유저의 총 획득 포인트 조회 및 포인트 상세내역 조회 가능.
```json
{
  "userId": "85cd3af1-124e-4699-abdd-449b0129961a",
  "point": 2,
  "userPointDetails": [
    {
      "placeId": "98fbf902-e805-4b16-9ff8-338e65c23b34",
      "pointType": "FIRST_REVIEW",
      "point": 1
    },
    {
      "placeId": "98fbf902-e805-4b16-9ff8-338e65c23b34",
      "pointType": "PHOTO",
      "point": 1
    }
  ]
}
```

## 데이터 베이스 ERD
<img width="516" alt="스크린샷 2022-07-11 오후 8 00 52" src="https://user-images.githubusercontent.com/33611355/178250305-62de83e6-8c6c-47fd-aa3c-406d11cda1ae.png">

## DDL Scripts
```sql
create table attach_file (
    attach_file_id BINARY(16) not null,
    created_date datetime(6),
    review_id BINARY(16),
    primary key (attach_file_id)
) engine=InnoDB

create table point (
    point_id BINARY(16) not null,
    created_date datetime(6),
    place_id BINARY(16),
    point integer not null,
    point_type varchar(255),
    user_id BINARY(16),
    review_id BINARY(16),
    primary key (point_id)
) engine=InnoDB
    
create table point_history (
    point_id BINARY(16) not null,
    created_date datetime(6),
    place_id BINARY(16),
    point integer not null,
    point_type varchar(255),
    user_id BINARY(16),
    primary key (point_id)
) engine=InnoDB
    
create table review (
   review_id BINARY(16) not null,
   content varchar(255),
   created_date datetime(6),
   last_modified_date datetime(6),
   place_id BINARY(16) not null,
   user_id BINARY(16) not null,
   primary key (review_id)
) engine=InnoDB
    
alter table point 
    add constraint POINT_UNIQUE unique (user_id, place_id, point_type)
    
alter table review 
    add constraint REVIEW_CONSTRAINTS_INDEX unique (place_id, user_id)
    
alter table attach_file 
    add constraint fk_attachFile_to_review 
    foreign key (review_id) 
    references review (review_id)
    
alter table point 
    add constraint fk_point_to_review 
    foreign key (review_id) 
    references review (review_id)
```