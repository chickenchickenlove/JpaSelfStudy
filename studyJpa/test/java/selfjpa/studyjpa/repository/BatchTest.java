package selfjpa.studyjpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;

import javax.persistence.EntityManager;
import java.util.List;

import static selfjpa.studyjpa.domain.QMember.member;
import static selfjpa.studyjpa.domain.QOrder.order;


@Transactional
@SpringBootTest
@Slf4j
class BatchTest {

    @Autowired
    EntityManager em;

    @Autowired
    MyRepository myRepository;


    @Autowired
    JPAQueryFactory queryFactory;

    private static Long number;




//    @BeforeEach
    void init() {
        for (int i = 0; i < 100000; i++) {
            Member member = new Member();
            em.persist(member);
        }

    }




    @Test
    @DisplayName("저장 : Batch 처리 X")
    void test1() {

        for (int i = 0; i < 100000; i++) {
            Member member = new Member();
            em.persist(member);
        }

    }


    @Test
    @DisplayName("저장 Batch 처리 O")
    void test2() {

        int cnt = 0 ;
        for (int i = 0; i < 100000; i++) {
            Member member = new Member();
            em.persist(member);


            if (i % 100 == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    @Test
    @DisplayName("저장 : Batch 처리 X + 수정 : 페이징으로 Batch 처리")
    void test3() {

        for (int i = 0; i < 1000; i++) {
            Member member = new Member();
            em.persist(member);
        }

        em.flush();
        em.clear();

        PageRequest page = PageRequest.of(0, 100);

        for (int i = 0; i < 10; i++) {

            List<Member> result = queryFactory.selectFrom(member)
                    .offset(page.getOffset())
                    .limit(page.getPageSize())
                    .fetch();

            for (Member member1 : result) {
                member1.setName("memberA");
            }

            em.flush();
            em.clear();


            PageRequest nextPage = PageRequest.of(page.getPageNumber(), 100);
            page = nextPage;
        }
    }


    @Test
    @DisplayName("Batch Update")
    void test4() {

        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            em.persist(member);
        }

        em.flush();
        em.clear();

        queryFactory.update(member)
                .set(member.name, "memberA")
                .execute();



    }





}