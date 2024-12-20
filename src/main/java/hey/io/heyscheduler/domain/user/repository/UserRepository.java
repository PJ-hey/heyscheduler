package hey.io.heyscheduler.domain.user.repository;

import hey.io.heyscheduler.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserQueryRepository {

    Optional<User> findByUserId(String userId);
}
