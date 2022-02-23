package selfjpa.studyjpa.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;
import selfjpa.studyjpa.domain.QOrder;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import static selfjpa.studyjpa.domain.QMember.member;
import static selfjpa.studyjpa.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryImpl {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;


    @Transactional
    public void saveOrder(Order order) {
        em.persist(order);
    }


    public Order findOrderById(Long orderId) {
        return queryFactory.selectFrom(order)
                .setLockMode(LockModeType.NONE)
                .fetchOne();
    }


    public Order findOrderByIdOptimisticForceIncrement(Long orderId) {
        return queryFactory.selectFrom(order)
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .fetchOne();
    }




}
