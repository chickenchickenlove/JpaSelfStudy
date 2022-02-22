package selfjpa.studyjpa.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;
import selfjpa.studyjpa.servcie.MemberService;
import selfjpa.studyjpa.servcie.OrderService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.*;
import static org.assertj.core.api.Assertions.*;

//@Transactional
@SpringBootTest
@Slf4j
public class LockTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepositoryImpl memberRepositoryImpl;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepositoryImpl orderRepository;


    private static int threadNum = 100;
    private static Long memberNum;





    @Test
    @DisplayName("JPA Repository Lock 적용 테스트")
    void test1() {

        Member member = new Member();
        member.setName("memberA");
        memberRepository.save(member);
        memberNum = member.getId();
        log.info("Member Num = {}", memberNum);


        for (int i = 0; i < 100; i++) {
            memberService.doSomethingJpaRepository(memberNum);
        }

        Member findMember = em.find(Member.class, memberNum);
        log.info("Result Version = {}", findMember.getVersion());
        assertThat(findMember.getVersion()).isEqualTo(100L);

    }



    @Test
    @DisplayName("JPQL 이용한 LOCK 적용")
    void test2() {

        Member member = new Member();
        member.setName("memberA");
        memberRepository.save(member);
        memberNum = member.getId();
        log.info("Member Num = {}", memberNum);


        for (int i = 0; i < 100; i++) {
            memberService.doSomethingJpqlQuery();
        }

        Member findMember = em.find(Member.class, memberNum);
        log.info("Result Version = {}", findMember.getVersion());
        assertThat(findMember.getVersion()).isEqualTo(100L);
    }




    @Test
    @DisplayName("Query DSL 이용한 LOCK 적용")
    void test3() {

        Member member = new Member();
        member.setName("memberA");
        memberRepository.save(member);
        memberNum = member.getId();
        log.info("Member Num = {}", memberNum);


        for (int i = 0; i < 100; i++) {
            memberService.doSomethingQueryDsl();
        }

        Member findMember = em.find(Member.class, memberNum);
        log.info("Result Version = {}", findMember.getVersion());
        assertThat(findMember.getVersion()).isEqualTo(100L);

    }

    @Test
    @DisplayName("Query DSL 이용한 LOCK 적용")
    void test4() {

        Member member = new Member();
        member.setName("memberA");
        memberRepository.save(member);
        memberNum = member.getId();
        log.info("Member Num = {}", memberNum);


        for (int i = 0; i < 100; i++) {
            memberService.doSomethingEntityManager(memberNum);
        }

        Member findMember = em.find(Member.class, memberNum);
        log.info("Result Version = {}", findMember.getVersion());
        assertThat(findMember.getVersion()).isEqualTo(100L);

    }



    @Test
    @DisplayName("None Type Test : 업데이트 없으므로 쿼리가 나가지 않음. ")
    void test5() {
        Member member = new Member();
        member.setName("memberA");
        memberRepository.save(member);
        memberNum = member.getId();

        memberService.noneLockTypeUpdateOnlySelect();
    }




    @Test
    @DisplayName("None Type Test : 업데이트 쿼리 나감. 이 때, Where에 Version 정보가 들어가서 없으면 업데이트가 안됨.  ")
    void test6() {
        Member member = new Member();
        member.setName("memberA");
        memberRepository.save(member);
        memberNum = member.getId();

        memberService.noneLockTypeUpdate();
    }

    @Test
    @DisplayName("None Type Test : 조회 시점 Ver != 수정 시점 Version → 예외 발생   ")
    void test7() throws InterruptedException {
        Member member = new Member();
        member.setName("memberA");
        memberRepository.save(member);
        memberNum = member.getId();

        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 2; i++) {
            executorService.execute(() -> {
                memberService.noneLockTypeUpdate();
                latch.countDown();
            });
        }

        Thread.sleep(1000);

    }


    @Test
    @DisplayName("None Type Test : 연관관계의 주인 엔티티 추가 → Version은 바뀌지 않는다. ")
    void test8() {
        Member member = new Member();
        member.setName("memberA");
        Order order = new Order();
        order.addMember(member);
        memberRepositoryImpl.saveMember(member);

        memberService.noneLockTypeUpdateAddOrder();

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("Result Version = {}", findMember.getVersion());

    }


    @Test
    @DisplayName("None Type Test : 연관관계의 주인 값 수정 → Version은 바뀌지 않는다. ")
    void test9() {
        Member member = new Member();
        member.setName("memberA");
        Order order = new Order();
        order.addMember(member);
        memberRepositoryImpl.saveMember(member);

        memberService.noneLockTypeUpdateUpdateOrder();

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("Result Version = {}", findMember.getVersion());
    }

    @Test
    @DisplayName("None Type Test : 연관관계 주인의 Member 필드 수정 → Version 증가 ")
    void test11() {

        Member member = new Member();
        member.setName("memberA");
        Order order = new Order();
        order.addMember(member);
        memberRepositoryImpl.saveMember(member, order);

        log.info("Before Version of Order = {}, PK = {}", order.getVersion(), order.getId());

        // Order의 Member Field를 변경함
        orderService.orderAddMember(order.getId());

        Order findOrder = orderRepository.findOrderById(order.getId());
        log.info("After Version of Orders = {}, PK = {}", findOrder.getVersion(), findOrder.getId());
    }




    @Test
    @DisplayName("Optimistic Type Test : 단순 조회 시, Select 쿼리가 한번 더 나가는 것을 확인한다. ")
    void test12() {

        Member member = new Member();
        member.setName("memberA");
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());

        // Order의 Member Field를 변경함
        Member findMember = memberService.findMemberByMemberIdOptimistic(member.getId());
        log.info("After Version of Member = {}, PK = {}", findMember.getVersion(), findMember.getId());
    }

    @Test
    @DisplayName("Optimistic Type Test : 조회한 엔티티가 커밋 전 Version 정보가 수정되면 예외 발생  ")
    void test13() throws InterruptedException {

        Member member = new Member();
        member.setName("memberA");
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());

        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            memberService.findMemberByMemberIdOptimistic(member.getId());
            try {

                memberService.findMemberByMemberIdOptimistic(member.getId());
                Thread.sleep(1000);
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        executorService.execute(() -> {
            memberService.findMemberByMemberIdOptimisticAndUpdate(member.getId());
            latch.countDown();
        });

        latch.await();

    }


    @Test
    @DisplayName("Optimistic Force Increment Type Test : 연관관계 주인 필드 추가 시, Version 증가해야함. ")
    void test14() {

        Member member = new Member();
        member.setName("memberA");
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());


        // Member 엔티티의 Orders에 Order를 추가함.
        memberService.findMemberByMemberIdForceIncrementAndAddOrder(member.getId());

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("After Version of Member = {}, PK = {}", findMember.getVersion(), findMember.getId());
    }

    @Test
    @DisplayName("Optimistic Force Increment Type Test : 단순 조회 시, Version 1회 증가함. ")
    void test15() {

        Member member = new Member();
        member.setName("memberA");
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());

        // Member 엔티티 단순 조회
        memberService.findMemberByMemberIdForceIncrement(member.getId());

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("After Version of Member = {}, PK = {}", findMember.getVersion(), findMember.getId());
    }


    @Test
    @DisplayName("Optimistic Force Increment Type Test : 엔티티 수정 시 Version 2회 증가함. ")
    void test16() {

        Member member = new Member();
        member.setName("memberA");
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());

        // Order의 Member Field를 변경함
        memberService.findMemberByMemberIdForceIncrementAndEditMemberName(member.getId());

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("After Version of Member = {}, PK = {}", findMember.getVersion(), findMember.getId());
    }

    @Test
    @DisplayName("Optimistic Force Increment Type Test : 연관관계 주인 수정 시, Update 쿼리 2회 발생")
    void test17() {

        Member member = new Member();
        member.setName("memberA");
        Order order = new Order();
        order.addMember(member);
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());

        // Order의 Member Field를 변경함
        orderService.findOrderByIdForceIncrementAndEditOrderName(order.getId());

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("After Version of Member = {}, PK = {}", findMember.getVersion(), findMember.getId());
    }


    @Test
    @DisplayName("Optimistic Force Increment Type Test : 연관관계 주인의 가짜 객체 수정 시, 양 객체에 모두 업데이트 쿼리 발생")
    void test18() {

        Member member = new Member();
        member.setName("memberA");
        Order order = new Order();
        order.addMember(member);
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());

        // Order의 Member Field의 이름을 변경함.
        orderService.findOrderByIdForceIncrementAndEditMemberName(order.getId());

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("After Version of Member = {}, PK = {}", findMember.getVersion(), findMember.getId());
    }


    @Test
    @DisplayName("Optimistic Force Increment Type Test : 가짜 객체의 연관관계 수정 시, 양 객체에 모두 업데이트 쿼리 발생")
    void test19() {

        Member member = new Member();
        member.setName("memberA");
        Order order = new Order();
        order.addMember(member);
        memberRepositoryImpl.saveMember(member);
        log.info("Before Version of Member = {}, PK = {}", member.getVersion(), member.getId());

        // Member의 OrerList의 첫번째 Order의 이름을 변경함
        memberService.findMemberByMemberIdForceIncrementAndEditOrderName(member.getId());

        Member findMember = memberRepositoryImpl.findMemberById(member.getId());
        log.info("After Version of Member = {}, PK = {}", findMember.getVersion(), findMember.getId());
    }


}
