package com.baosight.gl.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.baosight.gl.mapper.db3", sqlSessionFactoryRef = "db3SqlSessionFactory")
@SuppressWarnings("all")
public class DataSourceConfig3 {

	@Bean("db3DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db3")
    public DataSource getDb3DataSource() {
        return DataSourceBuilder.create().build();
    }
//    public DataSource db2DataSource(DataSourceProperties properties){
////        return DataSourceBuilder.create().build();
//        //尝试修改linux发布出现的问题
//        return DataSourceBuilder.create(properties.getClassLoader())
//                .type(HikariDataSource.class)
//                .driverClassName(properties.determineDriverClassName())
//                .url(properties.determineUrl())
//                .username(properties.determineUsername())
//                .password(properties.determinePassword())
//                .build();
//    }

    @Bean("db3SqlSessionFactory")
    @Lazy
    public SqlSessionFactory db3SqlSessionFactory(@Qualifier("db3DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // mapper的xml形式文件位置必须要配置，不然将报错：no statement （这种错误也可能是mapper的xml中，namespace与项目的路径不一致导致）
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/db3/*.xml"));
        return bean.getObject();
    }
    @Bean(name = "db3JdbcTemplate")
    public JdbcTemplate db3JdbcTemplate(@Qualifier("db3DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    @Bean("db3SqlSessionTemplate")
    @Lazy
    public SqlSessionTemplate db3SqlSessionTemplate(@Qualifier("db3SqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}