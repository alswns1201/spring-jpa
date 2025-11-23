package com.spring.jpa.kafka.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EmailSendMessage {
    private String from;
    private String to ;
    private String subject;
    private String body;

}
