package com.spring.jpa.envers.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Audited
public class Book {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private int pages;
}