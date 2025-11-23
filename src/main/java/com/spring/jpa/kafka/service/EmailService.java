package com.spring.jpa.kafka.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jpa.kafka.dto.SendEmailRequestDto;
import com.spring.jpa.kafka.entity.EmailSendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final KafkaTemplate<String,String> kafkaTemplate;

    /**
     * 카프카에 message를 넣는 producer 작업.
     * @param sendEmailRequestDto
     */
    public void sendEmail(SendEmailRequestDto sendEmailRequestDto){
        EmailSendMessage emailSendMessage = new EmailSendMessage(
                sendEmailRequestDto.from(),
                sendEmailRequestDto.to(),
                sendEmailRequestDto.subject(),
                sendEmailRequestDto.body()
        );

        this.kafkaTemplate.send("email.send",toJsonString(emailSendMessage));
    }

    private String toJsonString(Object o){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String message = objectMapper.writeValueAsString(o);
            return message;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 직력화 실패");
        }
    }
}

