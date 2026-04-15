package curse.auth.repository;

import curse.auth.models.WishlistData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistDataRepository extends JpaRepository<WishlistData, Long> {
    List<WishlistData> findByUserId(Long userId);

    boolean existsByUserIdAndGiftId(Long userId, Long giftId);

    Optional<WishlistData> findByUserIdAndGiftId(Long userId, Long giftId);

    void deleteByUserIdAndGiftId(Long userId, Long giftId);
}
