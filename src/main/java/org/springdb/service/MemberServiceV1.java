package org.springdb.service;

import lombok.RequiredArgsConstructor;
import org.springdb.domain.Member;
import org.springdb.repository.MemberRepositoryV1;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // Transaction이 시작되고 => setAutoCommit = false로 시작하게 되는거다.
        // 트랜잭션을 사용하는 동안 같은 커넥션을 유지해야한다.
        // 다른 커넥션을 맺으면, 다른 세션에 맺히고, 트랜잭션도 완전 다른 곳에 맺히게 되는거다.
        // Application에서 같은 커넥션 유지? => 파라미터로 같은 커넥션 쓰도록 넘겨버린다.
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId,fromMember.getMoney()-money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney()+money);
        //여기서 커밋하거나 롤백하는 것을 선택해야한다. (트랜잭션 종료)
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
