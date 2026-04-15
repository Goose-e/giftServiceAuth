package curse.auth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "user_tag_weight")
public class UserTagWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_weight_id")
    private Long userTagWeightId;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tag_weight")
    private BigDecimal tagWeight;
}
