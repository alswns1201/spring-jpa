package com.spring.jpa.nplus1;

import com.spring.jpa.npuls1.entity.Crew;
import com.spring.jpa.npuls1.entity.Member;
import com.spring.jpa.npuls1.repository.CrewRepository;
import com.spring.jpa.npuls1.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class Nplus1Test {

    @Autowired
    CrewRepository crewRepository;
    @Autowired
    MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em; // 쿼리 로그를 확인하기 위해 EntityManager 주입

    @BeforeEach
    void setup() {
        for (int i = 1; i <= 3; i++) {
            Crew crew = new Crew();
            crew.setName("크루" + i);
            crewRepository.save(crew);
            for (int j = 1; j <= 5; j++) {
                Member member = new Member();
                member.setName("회원" + i + "-" + j);
                member.setCrew(crew);
                memberRepository.save(member);
            }
        }
        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화하여 실제 DB에서 조회하도록 함
    }

    @Test
    void nPlus1_발생예시() {
        System.out.println("### N+1 발생 예시 시작 ###");
        // 1. 모든 Member 조회 (쿼리 1번)
        List<Member> members = memberRepository.findAll();
        System.out.println("모든 회원 조회 완료");

        // 2. 각 Member의 Crew 이름을 조회 (N번의 추가 쿼리 발생)
        for (Member member : members) {
            System.out.println("회원 이름: " + member.getName() + ", 크루 이름: " + member.getCrew().getName());
        }
        System.out.println("### N+1 발생 예시 종료 ###");
        // 예상되는 쿼리 수:
        // - `memberRepository.findAll()`: 1번 (SELECT * FROM member)
        // - `member.getCrew().getName()`: List<Member>의 크기(15)만큼 추가 쿼리 발생 (SELECT * FROM crew WHERE id=?)
        // 총 1 + N (1 + 15 = 16)번의 쿼리가 발생하게 됩니다.

        System.out.println("### N+1 발생 예시 시작2 ###");

        // 1. 모든 Member 조회 (쿼리 1번)
        List<Crew> crews = crewRepository.findAll();
        System.out.println("모든 크루 조회 완료");

        // 2. 모든 크루의 멤버 수
        for (Crew crew : crews) {
            System.out.println("크루 이름: " + crew.getName() + ", 소속 회원 수: " + crew.getMembers().size());
        }
        System.out.println("### N+1 발생 예시 종료2 ###");


    }

    @Test
    void fetchJoin_해결예시() {
        System.out.println("### Fetch Join 해결 예시 시작 ###");
        // fetch join을 사용한 Member 조회
        // MemberRepository에 다음 메서드를 추가해야 합니다.
        // @Query("select m from Member m join fetch m.crew")
        // List<Member> findAllWithCrewFetchJoin();
        List<Member> members = memberRepository.findAllWithCrewFetchJoin();
        System.out.println("Fetch Join으로 모든 회원 조회 완료");

        // 각 Member의 Crew 이름을 조회 (추가 쿼리 없음)
        for (Member member : members) {
            System.out.println("회원 이름: " + member.getName() + ", 크루 이름: " + member.getCrew().getName());
        }
        System.out.println("### Fetch Join 해결 예시 종료 ###");


        List<Crew> crews = crewRepository.findAllWithMembersFetchJoin();
        for (Crew crew : crews){
            System.out.println("회원 수: " + crew.getMembers().size());
        }

        // 예상되는 쿼리 수:
        // - `memberRepository.findAllWithCrewFetchJoin()`: 1번 (SELECT m.*, c.* FROM member m JOIN crew c ON m.crew_id = c.id)
        // 총 1번의 쿼리가 발생하게 됩니다.
    }

    @Test
    void QueryDslFetchJoin_해결예시() {
        System.out.println("### Fetch Join 예시 전  ###");



        List<Crew> crews = crewRepository.findAllCrewsWithMembersByQuerydsl();
        for (Crew crew : crews){
            System.out.println("회원 수: " + crew.getMembers().size());
        }
        System.out.println("### Fetch Join 예시 ###");


        List<Crew> fetchJoinCrews = crewRepository.findAllCrewsWithMembersByQuerydslFetchJoin();
        for (Crew crew : fetchJoinCrews){
            System.out.println("회원 수: " + crew.getMembers().size());
        }

        System.out.println("### Fetch Join 예시 끝 ###");



        // 예상되는 쿼리 수:
        // - `memberRepository.findAllWithCrewFetchJoin()`: 1번 (SELECT m.*, c.* FROM member m JOIN crew c ON m.crew_id = c.id)
        // 총 1번의 쿼리가 발생하게 됩니다.
    }
}