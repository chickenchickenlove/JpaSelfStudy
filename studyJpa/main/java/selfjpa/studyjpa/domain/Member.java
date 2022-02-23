package selfjpa.studyjpa.domain;


import lombok.Data;
import lombok.ToString;

import javax.annotation.sql.DataSourceDefinitions;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(of = "name")
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;


    private String name;

    @Version
    private Long version;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orderList = new ArrayList<>();



}
