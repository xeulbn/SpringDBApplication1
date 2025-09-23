package org.springdb.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springdb.connection.ConnectionConst.*;


@Slf4j
class DBConnectionUtilTest {

    @Test
    void getConnection() {
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }

    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD); //DriverManager를 직접 사용할때는 100번 사용하면 100번전달
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection = {} , class = {}",con1,con1.getClass());
        log.info("connection = {} , class = {}",con2,con2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource); //커넥션 획득시 그냥 getConnection만 (설정과 사용을 분리)
    }

    @Test
    void dataSourceConnectionPool() throws Exception {
        //커넥션 풀링 -> Hikari사용
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000); //왜 별도의 쓰레드를 사용해서 커넥션을 채우는걸까 -> 애플리케이션 실행 시 커넥션풀 채울때까지 마냥 대기한다면 어플리케이션 실행 시간이 늦어진다.
    } //커넥션 풀이 안찼으면, 어플리케이션이 약간 기다려서, 내부적으로 커넥션 획득

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection(); //pool 1개
        Connection con2 = dataSource.getConnection(); //획득 Pool 1개 획득  10개까지는 이런식으로 동작
    }


}