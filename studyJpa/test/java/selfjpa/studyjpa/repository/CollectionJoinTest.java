package selfjpa.studyjpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static selfjpa.studyjpa.domain.QMember.member;
import static selfjpa.studyjpa.domain.QOrder.*;


@Transactional
@SpringBootTest
@Slf4j

class CollectionJoinTest {

    @Autowired
    EntityManager em;

    @Autowired
    MyRepository myRepository;


    @Autowired
    JPAQueryFactory queryFactory;



    @Test
    @DisplayName("일대다 관계 Fetch Join → 중복 행 2개 만들어짐.")
    void test1() {

        Member newMember = new Member();
        Order order1= new Order();
        Order order2 = new Order();

        newMember.setName("member1");
        order1.setName("order1");
        order2.setName("order2");

        order1.addMember(newMember);
        order2.addMember(newMember);

        em.persist(newMember);
        em.flush();
        em.clear();


        List<Member> findMember = queryFactory.selectFrom(member)
                .join(member.orderList, order).fetchJoin()
                .fetch();

        log.info("Fetch Join findMember Size = {}", findMember.size());
        assertThat(findMember.size()).isEqualTo(2);


    }


    @Test
    @DisplayName("일대다 관계 일반 Join → 중복 행 2개 만들어짐.")
    void test2() {

        Member newMember = new Member();
        Order order1= new Order();
        Order order2 = new Order();

        newMember.setName("member1");
        order1.setName("order1");
        order2.setName("order2");


        order1.addMember(newMember);
        order2.addMember(newMember);

        em.persist(newMember);
        em.flush();
        em.clear();

        List<Member> findMember = queryFactory.selectFrom(member)
                .join(member.orderList, order).fetchJoin()
                .fetch();

        log.info("Common Join findMember Size = {}", findMember.size());
        assertThat(findMember.size()).isEqualTo(2);
    }



    @Test
    @DisplayName("일대다 관계 Fetch Join + Distinct → 중복 행 제거됨.")
    void test3() {

        Member newMember = new Member();
        Order order1= new Order();
        Order order2 = new Order();

        newMember.setName("member1");
        order1.setName("order1");
        order2.setName("order2");

        order1.addMember(newMember);
        order2.addMember(newMember);

        em.persist(newMember);
        em.flush();
        em.clear();

        List<Member> findMember = queryFactory.selectFrom(member).distinct()
                .join(member.orderList, order).fetchJoin()
                .fetch();


        log.info("Fetch JOin findMember Size = {}", findMember.size());
        assertThat(findMember.size()).isEqualTo(1);



    }



    @Test
    @DisplayName("일대다 관계 Inner Join + Distinct → 중복 행 제거됨.")
    void test4() {

        Member newMember = new Member();
        Order order1= new Order();
        Order order2 = new Order();

        newMember.setName("member1");
        order1.setName("order1");
        order2.setName("order2");

        order1.addMember(newMember);
        order2.addMember(newMember);

        em.persist(newMember);
        em.flush();
        em.clear();

        List<Member> findMember = queryFactory.selectFrom(member).distinct()
                .join(member.orderList, order)
                .fetch();


        log.info("Common Join findMember Size = {}", findMember.size());
        assertThat(findMember.size()).isEqualTo(1);



    }

}