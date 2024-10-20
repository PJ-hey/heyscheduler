package hey.io.heyscheduler.domain.artist.repository;

import hey.io.heyscheduler.domain.artist.entity.Artist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findFirstByOrgName(String orgName);

    List<Artist> findByArtistUidIn(List<String> artistUids);
}
