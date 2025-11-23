package com.spring.jpa.kafka.dto;

public record SendEmailRequestDto(
        String from, String to , String subject, String body
) {
}
