### 🧭 개요

이 프로젝트는 Spring Boot 3.5.6 + JPA + MySQL (Docker) 환경에서 엔티티 이력 관리, N+1 문제 실험, Stream 활용을 학습하기 위한 스터디 프로젝트입니다.

현재 주요 테스트 대상:

*   Java Stream API
*   Hibernate Envers
*   N+1 문제 분석 및 해결


### ⚙️ 기술 스택

| 구성요소 | 기술              |
| :------- | :---------------- |
| Language | Java 17           |
| Framework| Spring Boot 3.5.6 |
| ORM      | Spring Data JPA   |
| Audit    | Hibernate Envers  |
| DB       | MySQL (Docker)    |
| Build    | Gradle            |
| Qclass   | querydsl-jpa:5.0.0:jakarta|

## Stream 관련
- 테스트를 위한 stocks Entity와 약 4만개 임시 데이터 준비 
<img width="376" height="195" alt="image" src="https://github.com/user-attachments/assets/9b18fbf2-0da0-40b4-a211-e86d5b236bd2" />

- API 시나리오
  
| Method	| Endpoint | 	설명 |
| :------- | :----------| :------ |
| GET	   | /stocks	| 4만개 데이터 조회 |
| GET	| /stocks/stream	| 4만개 데이터 STREAM 조회 |
| GET	| /stocks/live	|  실시간 변경 된 데이터 return |

- /stokcs로 조회를 하면 데이터가 모두 나올때 까지 기다리게 된다. 반면, stream(`StreamingResponseBody`) 으로 조회를 하면 전부 완료 되지 않아도 실시간으로 변환된 값들을 실시간으로 화면에 제공된다.

- API 시나리오
  
| Method	| Endpoint | 	설명 |
| :------- | :----------| :------ |
| GET	   | reactive/stocks/stream	| 4만개 데이터 조회 |
| GET	| reactive/stocks/live	|실시간 변경 된 데이터 return |

- `StreamingResponseBody` , `outputStream` 으로 직접 가공하지 않고, WebFlux를 이용하여 실시간 처리를 적용가능 또한, `이벤트 루프 방식`으로 실시간 업데이트 내역에 적합.



## 🧩 Envers 테스트
- 경로 패키지 /envers
- RevisionRepository를 상속하면 Book의 변경 이력을 손쉽게 조회 가능
### 🧪 API 시나리오
| Method	| Endpoint | 	설명 |
| :------- | :----------| :------ |
| POST	   | /saveBook	| 새로운 Book 저장 |
|PUT	| /update/{id}/{pages}	| Book의 페이지 수 수정 |
|DELETE	| /delete/{id}	| Book 삭제 |
|GET	| /getInfo/{id}	| 해당 Book의 최근 Revision 조회 |
```
Hibernate: select ba1_0.rev,ba1_0.id,ba1_0.revtype,ba1_0.name,ba1_0.pages,dre1_0.rev,dre1_0.revtstmp from book_aud ba1_0 join revinfo r2_0 on r2_0.rev=ba1_0.rev,revinfo dre1_0 where ba1_0.id=? and ba1_0.rev=dre1_0.rev order by r2_0.revtstmp desc limit ?
Optional[Revision 3 of entity Book(id=1, name=Spring in Action, pages=400) - Revision metadata DefaultRevisionMetadata{entity=DefaultRevisionEntity(id = 3, revisionDate = Oct 8, 2025, 2:53:20 PM), revisionType=UPDATE}]
```
<img width="589" height="238" alt="image" src="https://github.com/user-attachments/assets/b134a36b-86d0-4c5b-a2c0-10ca31e02af3" />


## 🧩 N+1 문제 테스트 

### 📄 엔티티 설정
- `Crew`와 `Member`는 `@OneToMany`, `@ManyToOne` 관계로 연결되어 있으며, 기본적으로 `fetch = FetchType.LAZY` (지연 로딩)으로 설정되어 있습니다.
### 🗂 Repository 설정
- N+1 문제 해결을 위한 Fetch Join 쿼리 메서드와 Querydsl 기반의 커스텀 Repository를 포함합니다.
### 테스트 코드에서 성능 확인 - 지연 로딩(FetchType.LAZY) 설정된 연관 관계에서 N+1 문제가 발생하는 시나리오를 보여줍니다.
### 시나리오 1 (Member -> Crew):
- memberRepository.findAll() 호출 시 1번의 쿼리가 발생하고, 이후 각 Member의 Crew 정보(member.getCrew().getName())에 접근할 때마다 N개의 추가 쿼리가 발생합니다. (총 1 + N 쿼리)
`예상 쿼리 수: 1 (findAll) + 15 (Member 수) = 16번 쿼리`
### 시나리오 2 (Crew -> Members):
- crewRepository.findAll() 호출 시 1번의 쿼리가 발생하고, 이후 각 Crew의 Member 목록(crew.getMembers().size())에 접근할 때마다 N개의 추가 쿼리가 발생합니다. (총 1 + N 쿼리)
`예상 쿼리 수: 1 (findAll) + 3 (Crew 수) = 4번 쿼리`

### 2. fetchJoin_해결예시() - @Query 어노테이션과 JOIN FETCH를 사용하여 N+1 문제를 해결하는 방법을 보여줍니다.
### 시나리오 1 (Member -> Crew):
- memberRepository.findAllWithCrewFetchJoin() 호출 시 1번의 쿼리만으로 모든 Member와 그에 해당하는 Crew 정보를 함께 가져옵니다.
  `예상 쿼리 수: 1번 쿼리`
### 시나리오 2 (Crew -> Members):
- crewRepository.findAllWithMembersFetchJoin() 호출 시 1번의 쿼리만으로 모든 Crew와 그에 해당하는 Member 목록을 함께 가져옵니다.
`예상 쿼리 수: 1번 쿼리`
- 주의: @OneToMany 관계에서 Fetch Join 사용 시 데이터 중복이 발생할 수 있으므로, Set 컬렉션 사용 또는 JPQL의 DISTINCT 키워드 활용을 고려해야 합니다.
### 3. queryDslFetchJoin_해결예시() - Querydsl을 활용하여 N+1 문제가 발생하는 경우와 fetchJoin() 메서드를 사용하여 이를 해결하는 방법을 비교합니다.
- Querydsl 일반 조회 (N+1 발생 가능): crewRepository.findAllCrewsWithMembersByQuerydsl() 호출 시 1번의 쿼리가 발생하고, 이후 각 Crew의 Member 목록(crew.getMembers().size())에 접근할 때마다 N개의 추가 쿼리가 발생합니다.
`예상 쿼리 수: 1 (Querydsl 조회) + 3 (Crew 수) = 4번 쿼리`
- Querydsl Fetch Join 해결: crewRepository.findAllCrewsWithMembersByQuerydslFetchJoin() 호출 시 fetchJoin()을 통해 1번의 쿼리로 Crew와 연관된 Member 목록까지 함께 가져옵니다.
`예상 쿼리 수: 1번 쿼리`

