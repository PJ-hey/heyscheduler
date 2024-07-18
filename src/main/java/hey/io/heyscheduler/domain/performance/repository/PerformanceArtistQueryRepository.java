//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.artist.dto.ArtistListResponse;
import java.util.List;

public interface PerformanceArtistQueryRepository {
    List<ArtistListResponse> getArtistsByPerformanceStartDate();
}
