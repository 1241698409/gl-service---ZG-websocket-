package com.baosight.gl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author SY
 */
@MapperScan("com.baosight.gl.mapper")
@SpringBootApplication
public class GlBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(GlBootApplication.class, args);
	}
}
