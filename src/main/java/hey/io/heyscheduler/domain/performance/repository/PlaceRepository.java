package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.entity.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByPlaceUid(String placeUid);
}
