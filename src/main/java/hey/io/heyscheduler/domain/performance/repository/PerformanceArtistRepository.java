//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.domain.PerformanceArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceArtistRepository extends JpaRepository<PerformanceArtist, Long>, PerformanceArtistQueryRepository {
}
