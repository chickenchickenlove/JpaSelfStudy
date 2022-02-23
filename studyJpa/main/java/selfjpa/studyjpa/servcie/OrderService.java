package selfjpa.studyjpa.servcie;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import selfjpa.studyjpa.domain.Member;
import selfjpa.studyjpa.domain.Order;
import selfjpa.studyjpa.repository.MemberRepository;
import selfjpa.studyjpa.repository.MemberRepositoryImpl;
import selfjpa.studyjpa.repository.OrderRepositoryImpl;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepositoryImpl orderRepository;
    private final MemberRepositoryImpl memberRepository;

    @Transactional
    public void orderAddMember(Long orderId) {
        Order findOrder = orderRepository.findOrderById(orderId);
        Member member = new Member();
        findOrder.addMember(member);
        memberRepository.saveMember(member);
    }


    @Transactional
    public void findOrderByIdForceIncrementAndEditOrderName(Long orderId) {
        Order findOrder = orderRepository.findOrderByIdOptimisticForceIncrement(orderId);
        findOrder.setName("update");
    }


    @Transactional
    public void findOrderByIdForceIncrementAndEditMemberName(Long orderId) {
        Order findOrder = orderRepository.findOrderByIdOptimisticForceIncrement(orderId);
        findOrder.getMember().setName("update");
    }

    @Transactional
    public void orderEdit(Long orderId) {
        Order findOrder = orderRepository.findOrderById(orderId);
        findOrder.setName(UUID.randomUUID().toString());
    }



}
