package com.baosight.gl.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@MapperScan(basePackages = "com.baosight.gl.mapper.db1", sqlSessionFactoryRef = "db1SqlSessionFactory")
@SuppressWarnings("all")
public class DataSourceConfig1 {

	/**
	 * 读取application.properties中的配置参数映射成为一个对象
	 * 表示这个数据源是默认数据源, 这个注解必须要加，因为不加的话spring将分不清楚那个为主数据源（默认数据源）
	 */
	@Primary
	@Bean("db1DataSource")
	@ConfigurationProperties(prefix = "spring.datasource.db1")
	    public DataSource getDb1DataSource() {
        return DataSourceBuilder.create().build();
    }
//	public DataSource db1DataSource(DataSourceProperties properties){
////        return DataSourceBuilder.create().build();
//		//尝试修改linux发布出现的问题
//		return DataSourceBuilder.create(properties.getClassLoader())
//				.type(HikariDataSource.class)
//				.driverClassName(properties.determineDriverClassName())
//				.url(properties.determineUrl())
//				.username(properties.determineUsername())
//				.password(properties.determinePassword())
//				.build();
//	}

	/**
	 * @param dataSource
	 * @return
	 *
	 * @throws Exception
	 */
	@Primary
	@Bean("db1SqlSessionFactory")
	public SqlSessionFactory db1SqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/db1/*.xml"));
		return bean.getObject();
	}
	@Primary
	@Bean(name = "db1JdbcTemplate")
	public JdbcTemplate db1JdbcTemplate(@Qualifier("db1DataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	/**
	 * x
	 * @param sqlSessionFactory
	 * @return
	 */
	@Primary
	@Bean("db1SqlSessionTemplate")
	public SqlSessionTemplate db1SqlSessionTemplate(@Qualifier("db1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}