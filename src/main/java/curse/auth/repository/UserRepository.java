package curse.auth.repository;

import curse.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    List<User> findByUsernameContainingIgnoreCase(String username);
}
