package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long>, PerformanceQueryRepository {

    boolean existsPerformanceByPerformanceUid(String performanceUid);
}
