package com.spring.jpa.npuls1.repository;

import com.spring.jpa.npuls1.entity.Crew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrewRepository extends JpaRepository<Crew,String> {

    // Fetch Join을 사용하여 Crew와 Member를 함께 조회
    // distinct 키워드를 사용하여 Crew 엔티티 중복을 제거합니다.
    @Query("select distinct c from Crew c join fetch c.members")
    List<Crew> findAllWithMembersFetchJoin();

}
