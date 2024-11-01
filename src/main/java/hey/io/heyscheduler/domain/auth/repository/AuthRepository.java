package hey.io.heyscheduler.domain.auth.repository;

import hey.io.heyscheduler.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Auth, String>, AuthQueryRepository {

}
