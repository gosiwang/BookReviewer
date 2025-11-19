# 📚 Book Review - 도서 리뷰 프로젝트

Spring Boot, Spring Security, Spring Data JPA를 활용하여 구현한 도서 검색 및 리뷰 웹 애플리케이션입니다. 사용자는 도서를 검색하고, 로그인 후 원하는 도서에 대한 리뷰를 작성, 수정, 삭제할 수 있습니다.

![Animation](https://github.com/user-attachments/assets/481a0781-e864-4232-bc3d-b5cf46130773)

## 📅 개발 기간

  * **총 개발 기간:** 2024년 11월 20일 \~ 2024년 11월 29일
  * **개발 인원:** 1인 (개인 프로젝트)

-----

## 🚀 주요 기능

### 1\. 🙍‍♂️ 회원 관리 (Spring Security)

  * **회원가입:** `BCryptPasswordEncoder`를 사용한 비밀번호 암호화를 통해 안전하게 사용자 정보를 저장합니다.
  * **로그인/로그아웃:** Spring Security의 폼 로그인을 커스터마이징하여 인증을 관리합니다.
  * **접근 제어:** 인증된 사용자만 리뷰 작성/수정/삭제가 가능하도록 `isAuthenticated()` 및 `@PreAuthorize` 어노테이션을 활용하여 페이지 접근 권한을 제어합니다.

### 2\. 📖 도서 관리

  * **도서 검색:** 제목 또는 저자명에 키워드가 포함된 도서를 검색할 수 있습니다.
  * **검색 결과:** 검색된 도서 목록과 함께, 각 도서에 달린 리뷰 목록을 함께 조회합니다.

### 3\. ✍️ 리뷰 관리 (CRUD)

  * **리뷰 작성 (Create):** 로그인한 사용자는 특정 도서에 대해 1회만 리뷰를 작성할 수 있습니다. (중복 작성 방지)
  * **리뷰 조회 (Read):** 도서 검색 결과 페이지에서 각 도서별 리뷰 목록(작성자, 평점, 내용)을 조회할 수 있습니다.
  * **리뷰 수정 (Update):** `Principal` 객체를 활용하여 현재 로그인한 사용자와 리뷰 작성자가 일치하는 경우에만 '수정' 버튼이 노출되며, 수정 권한을 부여합니다.
  * **리뷰 삭제 (Delete):** 리뷰 작성자 본인만 리뷰를 삭제할 수 있도록 서버 측에서 인가(Authorization) 처리를 구현했습니다.

-----

## 🛠️ 적용 기술

  * **Backend:** Java 21, Spring Boot 3.4.0
  * **Database:** H2 (In-memory), Spring Data JPA
  * **Security:** Spring Security
  * **Template Engine:** Thymeleaf (Thymeleaf-Extras-SpringSecurity)
  * **Build Tool:** Gradle
  * **ETC:** Lombok

-----

## 📊 ERD (Entity Relationship Diagram)

프로젝트의 핵심 엔티티 간의 관계입니다.

```
+---------+       (1) --- (N)       +---------+
|  Users  |                         |  Review |
+---------+       (1) --- (N)       +---------+
| id      |                         | id      |
| username|-------------------------| user_id | (FK)
| password|                         | book_id | (FK)
| email   |                         | content |
+---------+                         | rating  |
                                    | ...     |
                                    +---------+
                                        | (N)
                                        |
                                        | (1)
+---------+                         +---------+
|  Book   |                         |         |
+---------+                         |         |
| id      |-------------------------|         |
| title   |                         |         |
| author  |                         |         |
| isbn    |                         |         |
+---------+                         +---------+
```

  * **Users (1)** : **Review (N)** - 한 명의 유저는 여러 개의 리뷰를 작성할 수 있습니다.
  * **Book (1)** : **Review (N)** - 하나의 책에는 여러 개의 리뷰가 달릴 수 있습니다.

-----

## ⚙️ 주요 구현 내용

### 1\. Spring Security를 이용한 인증 및 인가

  * `SecurityConfig` 파일을 통해 HTTP 요청에 대한 보안 규칙을 설정했습니다.
  * `/review/create`, `/review/edit/**`, `/review/delete/**` 등 민감한 작업은 `authenticated()`를 통해 인증된 사용자만 접근할 수 있도록 제한했습니다.
  * `UserSecurityService`가 `UserDetailsService`를 구현하여, 사용자명(username)으로 `Users` 엔티티를 조회하고 Spring Security가 사용할 수 있는 `UserDetails` 객체로 변환합니다.
  * `ReviewController`의 수정/삭제 메서드에 `@PreAuthorize("isAuthenticated()")`와 **`Principal` 객체를** 활용하여, **작성자 본인만**이 해당 리소스에 접근할 수 있도록 2중으로 권한을 검사했습니다.

### 2\. Spring Data JPA를 활용한 데이터 관리

  * `Users`, `Book`, `Review` 엔티티를 정의하고, `@ManyToOne`, `@JoinColumn` 등을 통해 엔티티 간의 연관 관계를 명확히 설정했습니다.
  * `BookRepository`에서는 \*\*JPQL(Java Persistence Query Language)\*\*과 `@Query` 어노테이션을 사용하여, **제목(title) 또는 저자(author) 중 하나라도** 키워드를 포함하면 검색되는 커스텀 검색 로직(`searchBooks`)을 구현했습니다.
  * `ReviewRepository`에서는 `existsByUserAndBook_Id`와 같은 쿼리 메서드를 사용하여, 특정 사용자가 특정 책에 대해 이미 리뷰를 작성했는지 효율적으로 검사하는 로직을 구현했습니다.

### 3\. 트랜잭션 및 예외 처리

  * `ReviewService`의 생성, 수정, 삭제 로직에 `@Transactional` 어노테이션을 사용하여 데이터의 일관성을 보장했습니다.
  * `bookRepository.findById().orElseThrow()` 또는 `HttpStatus.FORBIDDEN`을 발생시키는 `ResponseStatusException` 등을 활용하여, 데이터가 존재하지 않거나 권한이 없는 경우에 대한 예외 처리를 명확하게 구현했습니다.

-----

## 🚀 프로젝트 실행 방법

1.  **Git Clone:**

    ```bash
    git clone https://github.com/gosiwang/BookReviewer.git
    cd bookreview
    ```

2.  **프로젝트 빌드 및 실행 (Gradle):**

    ```bash
    ./gradlew bootRun
    ```

      * (또는 IntelliJ, Eclipse 등의 IDE에서 `BookreviewApplication.java` 파일을 직접 실행합니다.)

3.  **애플리케이션 접속:**

      * 메인 페이지: `http://localhost:8080/books`
      * H2 데이터베이스 콘솔: `http://localhost:8080/h2-console`
          * **JDBC URL:** `jdbc:h2:mem:testdb`
          * **User Name:** `sa`
          * (Password는 비워둡니다)
      * *프로젝트 실행 시 `data.sql` 파일이 자동으로 실행되어 초기 도서 데이터가 삽입됩니다.*
