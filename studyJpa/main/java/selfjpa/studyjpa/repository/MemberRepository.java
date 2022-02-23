package selfjpa.studyjpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import selfjpa.studyjpa.domain.Member;

import javax.persistence.LockModeType;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query(value = "select m from Member m where m.id=:id")
    Member findMemberById(@Param(value = "id") Long id);
}
