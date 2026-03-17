package com.collaboportal.common.database.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@AutoConfiguration
@MapperScan(basePackages = {
        "com.collaboportal.common.login.mapper",
        "com.collaboportal.common.oauth2.repository",
        "com.collaboportal.common.jwt.repository",
        "com.collaboportal.spnohin.repository"
})
public class DatabaseAutoConfiguration {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
        try {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            bean.setMapperLocations(
                    new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/mapper/**/*.xml"));
            new PathMatchingResourcePatternResolver().getResources("classpath*:com/collaboportal/**/repository/*.xml");
            bean.setTypeAliasesPackage("com.collaboportal.common.jwt.entity,com.collaboportal.common.login.model.DTO");
            return bean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
