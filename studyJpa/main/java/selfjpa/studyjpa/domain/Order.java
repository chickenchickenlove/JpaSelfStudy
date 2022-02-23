package selfjpa.studyjpa.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString(of = "name")
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;


    @Version
//    @Transient
    private Long version;



    public void addMember(Member member) {
        this.member = member;
        member.getOrderList().add(this);
    }

}
