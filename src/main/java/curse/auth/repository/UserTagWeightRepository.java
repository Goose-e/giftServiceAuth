package curse.auth.repository;

import curse.auth.models.UserTagWeight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTagWeightRepository extends JpaRepository<UserTagWeight, Long> {
    List<UserTagWeight> findByUserId(Long userId);

    Optional<UserTagWeight> findByUserIdAndTagId(Long userId, Long tagId);
}
