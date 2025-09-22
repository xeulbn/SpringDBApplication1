package org.springdb.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springdb.domain.Member;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        //save
        Member memberV0 = new Member("memberV0", 10000);
        repository.save(memberV0);
    }

    @Test
    void find() throws SQLException {
        Member memberV2 = new Member("memberV2", 10000);
        repository.save(memberV2);

        //findById
        Member findMember = repository.findById(memberV2.getMemberId());
        log.info("findMember = {}",findMember);
        Assertions.assertThat(findMember).isEqualTo(memberV2);
    }

    @Test
    void update()throws SQLException {
        Member member = new Member("memberV4",100000);
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