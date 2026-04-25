package curse.auth.repository;

import curse.auth.models.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByTag_TagIdIn(List<Long> tagIds);
}
