package curse.auth.repository;

import curse.auth.models.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VictimRepository extends JpaRepository<Victim, Long> {
    List<Victim> findByUserId(Long userId);

    @Query("select v from Victim v where v.userId =:userId and v.victimId =:victimId")
    Optional<Victim> findByVictimIdAndUserId(Long victimId, Long userId);
}
