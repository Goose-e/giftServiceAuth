package curse.auth.repository;

import curse.auth.models.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserId(Long userId);

    boolean existsByUserIdAndFriendUserId(Long userId, Long friendUserId);

    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

    void deleteByUserIdAndFriendUserId(Long userId, Long friendUserId);
}
