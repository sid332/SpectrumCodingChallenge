package com.SpectrumCode.spectrumCodeChallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
public class SpectrumCodeChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpectrumCodeChallengeApplication.class, args);
	}

}
