package org.springdb.repository;


import lombok.extern.slf4j.Slf4j;
import org.springdb.connection.DBConnectionUtil;
import org.springdb.domain.Member;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - ConnectionParam
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource; //Hikari도 dataSource를 사용하기 때문에 가능

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate(); //영향받은 row 수 만큼의 숫자반환
            return member;
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(connection,preparedStatement,null); //항상 호출의 보장
        }

    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con= null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()){ //next 호출시, 첫번째 데이터가 있는지 없는지 확인하는 것
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("Member not found memberID = "+memberId);
            }
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con,pstmt,rs); //해지는 순서대로
        }
    }

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()){ //next 호출시, 첫번째 데이터가 있는지 없는지 확인하는 것
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("Member not found memberID = "+memberId);
            }
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            //connection은 여기서 닫지 않는다.

            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void update(String memberId, int money)throws SQLException {
        String sql = "update member set money =? where member_id = ?";
        Connection con= null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}",resultSize);
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con,pstmt,null);
        }
    }

    public void update(Connection con, String memberId, int money)throws SQLException {
        String sql = "update member set money =? where member_id = ?";

        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}",resultSize);
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String memberId)throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con= null;
        PreparedStatement pstmt = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}",resultSize);
        }catch(SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con,pstmt,null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con); //계속 반환을 해주는거지.. 지속적인 반환
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get Connection ={}, class ={}",con,con.getClass());
        return DBConnectionUtil.getConnection();
    }
}
