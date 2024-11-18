package hey.io.heyscheduler.domain.performance.repository;

import hey.io.heyscheduler.domain.performance.dto.PerformanceDTO.PerformanceArtistDTO;
import hey.io.heyscheduler.domain.performance.dto.PerformanceDTO.PerformanceTicketingDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceQueryRepository {

    /**
     * <p>출연 아티스트 목록</p>
     *
     * @param performanceIds 공연 ID 목록
     * @return 아티스트 목록
     */
    List<PerformanceArtistDTO> selectPerformanceArtistList(List<Long> performanceIds);

    /**
     * <p>티켓 예매 공연 목록</p>
     *
     * @param currentDateTime 조회 기준 시각
     * @return 공연 목록
     */
    List<PerformanceTicketingDTO> selectPerformanceTicketingList(LocalDateTime currentDateTime);
}
