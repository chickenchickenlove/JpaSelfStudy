package selfjpa.studyjpa.servcie;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;
import selfjpa.studyjpa.repository.MemberRepository;
import selfjpa.studyjpa.repository.MemberRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberRepositoryImpl memberRepositoryImpl;


    @Transactional
    public void doSomethingJpaRepository(Long memberId) {
        memberRepository.findMemberById(memberId);
    }


    @Transactional
    public void doSomethingEntityManager(Long memberId) {
        memberRepositoryImpl.simpleSelect(memberId, 1);
    }

    @Transactional
    public void doSomethingQueryDsl() {
        memberRepositoryImpl.simpleSelectAllByQueryDsl();
    }


    @Transactional
    public void doSomethingJpqlQuery() {
        memberRepositoryImpl.simpleSelectAllByJpqlQuery();
    }

    @Transactional
    public Member noneLockTypeUpdateOnlySelect() {
        Member member = memberRepositoryImpl.simpleUpdateByNone();
        return member;
    }

    @Transactional
    public Member noneLockTypeUpdate() {
        Member member = memberRepositoryImpl.simpleUpdateByNone();
        member.setName("update" + UUID.randomUUID().toString());
        return member;
    }


    @Transactional
    public Member noneLockTypeUpdateAddOrder() {
        Member member = memberRepositoryImpl.simpleUpdateByNone();
        Order order = new Order();
        order.addMember(member);
        return member;
    }

    @Transactional
    public Member noneLockTypeUpdateUpdateOrder() {
        Member member = memberRepositoryImpl.simpleUpdateByNone();

        member.getOrderList().get(0).setName("orderQ");
        return member;
    }


    @Transactional
    public Member findMemberByMemberIdOptimistic(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdWithOptimistic(memberId);
        return member;
    }

    @Transactional
    public Member findMemberByMemberIdOptimisticAndUpdate(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdWithOptimistic(memberId);
        member.setName("update");

        return member;
    }


    @Transactional
    public Member findMemberByMemberIdForceIncrementAndAddOrder(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdWithForceIncrement(memberId);

        Order order = new Order();
        order.addMember(member);

        return member;
    }

    @Transactional
    public Member findMemberByMemberIdForceIncrement(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdWithForceIncrement(memberId);

        return member;
    }


    @Transactional
    public Member findMemberByMemberIdForceIncrementAndEditMemberName(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdWithForceIncrement(memberId);
        member.setName("update");
        return member;
    }


    @Transactional
    public Member findMemberByMemberIdForceIncrementAndEditOrderName(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdWithForceIncrement(memberId);
        member.getOrderList().get(0).setName("update");

        return member;
    }

    @Transactional
    public String findMemberNameByMemberIdOptimistic(Long memberId) {
        return memberRepositoryImpl.findMemberNameByMemberIdWithOptimistic(memberId);
    }



    @Transactional
    public Member findMemberByMemberIdPessimisticWrite(Long memberId) {
        return memberRepositoryImpl.findMemberByMemberIdPessimisticWrite(memberId);
    }

    @Transactional
    public Member findMemberByMemberIdPessimisticForceIncrement(Long memberId) {
        return memberRepositoryImpl.findMemberByMemberIdPessimisticForceIncrement(memberId);
    }

    @Transactional
    public Member findMemberByMemberIdPessimisticForceIncrementEditMember(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdPessimisticForceIncrement(memberId);
        member.setName("update");
        return member;
    }

    @Transactional
    public Member findMemberByMemberIdPessimisticForceIncrementEditOrder(Long memberId) {
        Member member = memberRepositoryImpl.findMemberByMemberIdPessimisticForceIncrement(memberId);
        member.getOrderList().get(0).setName("orderAAAAA");
        return member;
    }






}
