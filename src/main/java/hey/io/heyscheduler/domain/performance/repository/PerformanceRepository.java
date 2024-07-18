//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, String>, PerformanceQueryRepository {
}
