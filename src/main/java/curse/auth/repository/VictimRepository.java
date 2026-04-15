package curse.auth.repository;

import curse.auth.models.Victim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VictimRepository extends JpaRepository<Victim, Long> {
    List<Victim> findByUserId(Long userId);

    Optional<Victim> findByVictimIdAndUserId(Long victimId, Long userId);
}
