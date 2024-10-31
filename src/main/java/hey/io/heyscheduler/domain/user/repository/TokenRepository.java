package hey.io.heyscheduler.domain.user.repository;

import hey.io.heyscheduler.domain.user.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

}
