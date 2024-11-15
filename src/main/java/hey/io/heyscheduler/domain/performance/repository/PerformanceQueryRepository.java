package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.dto.PerformanceArtistDTO;
import java.util.List;

public interface PerformanceQueryRepository {

    /**
     * <p>출연 아티스트 목록</p>
     *
     * @param performanceIds 공연 ID 목록
     * @return 아티스트 목록
     */
    List<PerformanceArtistDTO> selectPerformanceArtistList(List<Long> performanceIds);
}
