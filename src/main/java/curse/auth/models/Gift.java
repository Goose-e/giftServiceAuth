package curse.auth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "tag_id")
    private Long tagId;
}
