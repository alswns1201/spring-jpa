package com.spring.jpa.npuls1.repository;

import com.spring.jpa.npuls1.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,String> {

    @Query("select m from Member m join fetch m.crew")
    List<Member> findAllWithCrewFetchJoin();
}
