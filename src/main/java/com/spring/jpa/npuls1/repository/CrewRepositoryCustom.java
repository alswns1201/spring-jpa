package com.spring.jpa.npuls1.repository;

import com.spring.jpa.npuls1.entity.Crew;

import java.util.List;

public interface CrewRepositoryCustom {
    List<Crew> findAllCrewsWithMembersByQuerydsl();
    List<Crew> findAllCrewsWithMembersByQuerydslFetchJoin();
}
