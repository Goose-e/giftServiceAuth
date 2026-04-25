package curse.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gifts")
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gifts_id")
    private Long giftId;

    @Column(name = "gift_name")
    private String giftName;

    @Column(name = "gift_avg_price")
    private Long giftAvgPrice;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
