package com.spring.jpa.npuls1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Crew {
    @Id
    @GeneratedValue
    private Long id;

    private String name;


    @OneToMany(mappedBy = "crew")
    private List<Member> members = new ArrayList<>();

    // 편의 관계 메소드
    public void addMember(Member member){
        members.add(member);
        member.setCrew(this);

    }

}
