//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.domain.Performance;
import java.util.List;

public interface PerformanceQueryRepository {
    List<String> findAllIds();

    List<Performance> getPerformancesByStartDate();
}
