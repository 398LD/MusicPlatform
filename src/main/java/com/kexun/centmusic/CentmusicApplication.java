package com.kexun.centmusic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.kexun.centmusic.data")
public class CentmusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(CentmusicApplication.class, args);
    }

}
