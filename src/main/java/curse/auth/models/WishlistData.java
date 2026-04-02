package curse.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "wishlist_data")
public class WishlistData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long wishlistId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "gift_id")
    private Long giftId;
}
