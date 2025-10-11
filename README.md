# N+1 기본 개념 

@ManyToOne 관계에서 LAZY 지연로딩시 N+1 예시 : 회원(N) - 크루(1) 관계시 
: 조회후 관계된 엔터티를 조회시 N 번 더 조회되는 현상.

<img width="851" height="545" alt="image" src="https://github.com/user-attachments/assets/8c6d50b7-655d-4894-bc3f-fab6d73e73de" />

<img width="1186" height="479" alt="image" src="https://github.com/user-attachments/assets/3fd1714e-533b-4b7d-97ba-07bc949acb7b" />
. 기본 해결 - fetch join  @OneToMany Fetch Join과 LIMIT/OFFSET (페이징)을 함께 사용할 경우, 데이터베이스 레벨에서 정확한 페이징을 수행할 수 없다고 판단하여 메모리에서 페이징을 처리하려고 시도합니다. 이는 대량의 데이터 조회 시 성능 문제를 야기합니다.
- @BatchSize를 활용한 N+1 쿼리 수 최적화
- Quertdsl 을 활용한 쿼리 적용.

# Spring Data Envers + RevisionRepository 테스트 README

## 프로젝트 개요
이 프로젝트는 Spring Boot 3 + JPA + MySQL 환경에서 **Hibernate Envers**를 사용하여 `Book` 엔티티의 변경 이력을 관리하고, `RevisionRepository`를 통해 최신/과거 Revision을 조회하는 예제입니다.  

- **Entity**: `Book` (`id`, `name`, `pages`)  
- **Controller**: `SpringDataEnversApplication`  
- **Repository**: `BookRepository` (extends `JpaRepository` + `RevisionRepository`)  

## 1. 엔티티 설정

```java
@Entity
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private int pages;
}
```
@Audited → 엔티티 변경 시 자동으로 _AUD 테이블 생성 및 이력 저장

## Repository 예제
```
public interface BookRepository extends JpaRepository<Book, Integer>, RevisionRepository<Book, Integer, Integer> {
}
```
RevisionRepository를 상속하면 엔티티 변경 이력 조회 가능


## REST API 테스트

revisionNumber → 변경 번호
revisionType → MOD (수정), ADD (생성), DEL (삭제)
entity → 해당 Revision 시점의 엔티티 상태

## 4. 테스트 시나리오

/saveBook로 책 생성

/update/{id}/{pages}로 페이지 수 수정

/delete/{id}로 책 삭제

/getInfo/{id}로 최근 Revision 조회

DB 확인: BOOK_AUD 테이블에서 모든 Revision 확인 가능

## 5. 참고

MySQL JDBC URL에 allowPublicKeyRetrieval=true 필요 (MySQL 8.x 이상)

spring.datasource.url=jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

ddl-auto=update 사용 시 개발용에만 적합, 운영에서는 Flyway/Liquibase 권장
