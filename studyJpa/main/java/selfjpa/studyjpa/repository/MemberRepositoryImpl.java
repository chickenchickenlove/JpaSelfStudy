package selfjpa.studyjpa.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;
import selfjpa.studyjpa.domain.QMember;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import java.util.List;

import static selfjpa.studyjpa.domain.QMember.member;
import static selfjpa.studyjpa.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;




    public void simpleSelect(Long memberId, int i) {
        Member member = em.find(Member.class, memberId);
//        em.lock(member, LockModeType.OPTIMISTIC_FORCE_INCREMENT); // 동작함`
//        Member member = em.find(Member.class, memberId,LockModeType.OPTIMISTIC_FORCE_INCREMENT); // JPA랏 먹음

    }

    @Transactional
    public Member findMemberById(Long id) {
        return queryFactory.selectFrom(member)
                .where(member.id.eq(id))
                .fetchOne();
    }


    public void simpleSelectAllByQueryDsl() {
        queryFactory.selectFrom(member)
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .fetch();
    }

    public void simpleSelectAllByJpqlQuery() {
        em.createQuery("select m from Member m")
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .getResultList();

    }


    public Member simpleUpdateByNone() {
        return queryFactory.selectFrom(member)
                .setLockMode(LockModeType.NONE)
                .fetchOne();

    }


    public Member findMemberByMemberIdWithOptimistic(Long memberId){
        return queryFactory.selectFrom(member)
                .setLockMode(LockModeType.OPTIMISTIC)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    public Member findMemberByMemberIdWithForceIncrement(Long memberId){
        return queryFactory.selectFrom(member)
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    public String findMemberNameByMemberIdWithOptimistic(Long memberId){
        return queryFactory.select(member.name)
                .from(member)
                .setLockMode(LockModeType.OPTIMISTIC)
                .where(member.id.eq(memberId))
                .fetchOne();
    }


    public Member findMemberByMemberIdPessimisticWrite(Long memberId) {
        return queryFactory.selectFrom(member)
                .where(member.id.eq(memberId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();
    }

    public Member findMemberByMemberIdPessimisticForceIncrement(Long memberId) {
        return queryFactory.selectFrom(member)
                .where(member.id.eq(memberId))
                .setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
                .fetchOne();
    }








    @Transactional
    public void saveMember(Member member) {
        em.persist(member);
    }


    @Transactional
    public void saveMember(Member member, Order order) {
        em.persist(member);
        em.persist(order);
    }




}
