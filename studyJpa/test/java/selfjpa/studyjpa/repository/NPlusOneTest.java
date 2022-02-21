package selfjpa.studyjpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;
import selfjpa.studyjpa.domain.QMember;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static selfjpa.studyjpa.domain.QMember.member;
import static selfjpa.studyjpa.domain.QOrder.order;


@Transactional
@SpringBootTest
@Slf4j
class NPlusOneTest {

    @Autowired
    EntityManager em;

    @Autowired
    MyRepository myRepository;


    @Autowired
    JPAQueryFactory queryFactory;

    private static Long number;




    @BeforeEach
    void init() {
        Member newMember1 = new Member();
        Member newMember2 = new Member();
        Member newMember3 = new Member();
        newMember1.setName("member1");
        newMember2.setName("member3");
        newMember3.setName("member3");



        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setName("order" + i);
            order.addMember(newMember1);
        }


        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setName("order" + i);
            order.addMember(newMember2);
        }


        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setName("order" + i);
            order.addMember(newMember3);
        }

        em.persist(newMember1);
        em.persist(newMember2);
        em.persist(newMember3);
        em.flush();
        em.clear();
        number = newMember1.getId();

    }




    @Test
    @DisplayName("N+1 문제 발생")
    void test1() {


        List<Member> members = queryFactory.selectFrom(member).fetch();
        int cnt = 0;
        for (Member member : members) {
            log.info("cnt = {} size = {}", cnt, member.getOrderList().size());
            cnt++;
        }
    }


    @Test
    @DisplayName("N+1 문제 해결 -> Fetch Join")
    void test2() {

        List<Member> members = queryFactory.selectFrom(member)
                .join(member.orderList, order)
                .fetchJoin()
                .fetch();
        int cnt = 0;
        for (Member member : members) {
            log.info("cnt = {} size = {}", cnt, member.getOrderList().size());
            cnt++;
        }
    }



    @Test
    @DisplayName("N+1 문제 해결 -> Batch Size")
    void test3() {

        List<Member> members = queryFactory.selectFrom(member)
                .fetch();
        int cnt = 0;
        for (Member member : members) {
            log.info("cnt = {} size = {}", cnt, member.getOrderList().size());
            cnt++;
        }

    }


    @Test
    @DisplayName("N+1 문제 해결 -> Batch Size + 페이징 쿼리 가능")
    void test4() {

        System.out.println("=================================================");

        List<Member> members = queryFactory.selectFrom(member)
                .offset(0)
                .limit(2)
                .fetch();
        int cnt = 0;
        for (Member member : members) {
            log.info("cnt = {} size = {}", cnt, member.getOrderList().size());
            cnt++;
        }

    }




}