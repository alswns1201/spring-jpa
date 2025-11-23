package com.spring.jpa.kafka.controller;


import com.spring.jpa.kafka.dto.SendEmailRequestDto;
import com.spring.jpa.kafka.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;


    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody SendEmailRequestDto sendEmailRequestDto){
        emailService.sendEmail(sendEmailRequestDto);
        return ResponseEntity.ok("이메일 발송 요청 완료 ");
    }


}
