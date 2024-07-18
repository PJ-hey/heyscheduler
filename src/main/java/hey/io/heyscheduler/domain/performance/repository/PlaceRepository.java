//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.domain.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    Optional<Place> findById(String id);
}
