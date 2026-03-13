package com.collaboportal.common.spring;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import okio.Path;

/**
 * MyBatis設定クラス
 * MyBatisのSqlSessionFactoryを設定する
 */
@AutoConfiguration
@MapperScan(basePackages = {
        "com.collaboportal.common.login.mapper",
        "com.collaboportal.common.oauth2.repository",
        "com.collaboportal.common.jwt.repository",
        "com.collaboportal.spnohin.repository"
})
public class MyBatisConfig {

    /**
     * SqlSessionFactoryを生成する
     * 
     * @param dataSource データソース
     * @return SqlSessionFactory
     * @throws RuntimeException SqlSessionFactoryの初期化に失敗した場合
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
        try {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            // データソースを設定
            bean.setDataSource(dataSource);

            // Mapper XMLファイルの場所を汎用的に設定
            bean.setMapperLocations(
                    new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/mapper/**/*.xml"));
            new PathMatchingResourcePatternResolver().getResources("classpath*:com/collaboportal/**/repository/*.xml");
            // エイリアスのパッケージを設定
            bean.setTypeAliasesPackage("com.collaboportal.common.jwt.entity,com.collaboportal.common.login.model.DTO");

            return bean.getObject();

        } catch (Exception e) {
            throw new RuntimeException("MyBatis SqlSessionFactory 初期化に失敗しました", e);
        }
    }
}
