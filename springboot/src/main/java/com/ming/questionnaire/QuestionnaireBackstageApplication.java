package com.ming.questionnaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync  // 开启异步请求
public class QuestionnaireBackstageApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuestionnaireBackstageApplication.class, args);
    }

}
