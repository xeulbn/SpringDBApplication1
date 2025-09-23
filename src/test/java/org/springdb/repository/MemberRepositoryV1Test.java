package org.springdb.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springdb.domain.Member;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springdb.connection.ConnectionConst.*;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        //기본 DriverManager - 항상 새로운 커넥션을 획득
//      DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);

        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        //save
        Member memberV0 = new Member("memberV15", 10000);
        repository.save(memberV0);
    }

    @Test
    void find() throws SQLException {
        Member memberV2 = new Member("memberV16", 10000);
        repository.save(memberV2);

        //findById
        Member findMember = repository.findById(memberV2.getMemberId());
        log.info("findMember = {}",findMember);
        Assertions.assertThat(findMember).isEqualTo(memberV2);
    }

    @Test
    void update()throws SQLException {
        Member member = new Member("memberV17",100000);
        repository.save(member);

        repository.update(member.getMemberId(), 200000);
        Member updateMember = repository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(200000);
    }

    @Test
    void delete() throws SQLException {
        Member member  = new Member("memberV5", 10000);
        repository.save(member);

        repository.delete(member.getMemberId());
        Assertions.assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }



}