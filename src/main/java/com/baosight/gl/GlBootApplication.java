package com.baosight.gl;

import com.baosight.gl.config.DataSourceConfig1;
import com.baosight.gl.config.DataSourceConfig2;
import com.baosight.gl.config.DataSourceConfig3;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author SY
 */
//多数据源不用加
//@MapperScan("com.baosight.gl.mapper")
//@SpringBootApplication(exclude = {MybatisAutoConfiguration.class})
@Import({DataSourceConfig1.class, DataSourceConfig2.class, DataSourceConfig3.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
//@SpringBootApplication
public class GlBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(GlBootApplication.class, args);
	}
}
