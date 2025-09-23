package org.springdb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdb.domain.Member;
import org.springdb.repository.MemberRepositoryV1;
import org.springdb.repository.MemberRepositoryV2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); //트랜잭션 시작
            //비즈니스 로직
            bizLogic(con, fromId, toId, money);
            con.commit(); //성공시 커밋
        }catch(Exception e){
            //여기서 커밋하거나 롤백하는 것을 선택해야한다. (트랜잭션 종료)
            con.rollback();
            throw new IllegalStateException(e);
        }finally{
            release(con);
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId,fromMember.getMoney()- money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney()+ money);
    }

    private static void release(Connection con) {
        if(con != null){
            try{
                con.setAutoCommit(true); //트랜잭션의 종료시, Autocommit을 꼭 다시 true로 변경 해줘야한다. Pool에 돌려줘야된다. (커넥션 풀 고려)
                con.close();
            }catch(Exception e){
                log.info("error",e);
            }
        }
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
