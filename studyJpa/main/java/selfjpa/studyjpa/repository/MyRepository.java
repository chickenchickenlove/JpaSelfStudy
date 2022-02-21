package selfjpa.studyjpa.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Transactional
@RequiredArgsConstructor
@Repository
public class MyRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;




}
