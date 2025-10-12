package com.spring.jpa.npuls1.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.jpa.npuls1.entity.Crew;
import com.spring.jpa.npuls1.entity.QCrew;
import com.spring.jpa.npuls1.entity.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CrewRepositoryCustomImpl implements CrewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Crew> findAllCrewsWithMembersByQuerydsl() {
        QCrew crew = QCrew.crew;
        // N+1 문제가 발생하는 Querydsl 쿼리 (fetchJoin() 미사용)
        return queryFactory
                .selectFrom(crew)
                .fetch();
    }

    @Override
    public List<Crew> findAllCrewsWithMembersByQuerydslFetchJoin() {
        QCrew crew = QCrew.crew;
        QMember member = QMember.member; // Member Q-Class

        // Fetch Join을 사용하여 N+1 문제 해결
        return queryFactory
                .selectFrom(crew)
                .leftJoin(crew.members, member).fetchJoin() // Crew의 members 컬렉션을 Fetch Join
                .distinct() // 중복 제거 (필요 시)
                .fetch();
    }
}
