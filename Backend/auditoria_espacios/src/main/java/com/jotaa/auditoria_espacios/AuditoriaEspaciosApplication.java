package com.jotaa.auditoria_espacios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuditoriaEspaciosApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditoriaEspaciosApplication.class, args);
		System.out.println("""
				_________________________________
				|								|
				|		Auditoria Espacios		| 
				|		 Backend iniciado		|
				|								|
				|		dev: Juan Mantilla		|
				|								|
				|_______________________________|				""");
	}

}
