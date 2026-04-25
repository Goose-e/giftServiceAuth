package curse.auth.repository;

import curse.auth.models.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByTag_TagIdIn(List<Long> tagIds);
    Optional<Gift> findByGiftNameIgnoreCase(String giftName);
}
