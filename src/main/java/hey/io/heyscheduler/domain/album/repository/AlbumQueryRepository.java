package hey.io.heyscheduler.domain.album.repository;

import java.util.List;

public interface AlbumQueryRepository {

    List<String> findAllIds();

}
