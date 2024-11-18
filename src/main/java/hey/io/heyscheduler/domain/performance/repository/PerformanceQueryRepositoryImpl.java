package hey.io.heyscheduler.domain.performance.repository;

import static hey.io.heyscheduler.domain.artist.entity.QArtist.artist;
import static hey.io.heyscheduler.domain.performance.entity.QPerformance.performance;
import static hey.io.heyscheduler.domain.performance.entity.QPerformanceArtist.performanceArtist;
import static hey.io.heyscheduler.domain.performance.entity.QPerformanceTicketing.performanceTicketing;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import hey.io.heyscheduler.common.repository.Querydsl5RepositorySupport;
import hey.io.heyscheduler.domain.artist.enums.ArtistStatus;
import hey.io.heyscheduler.domain.performance.dto.PerformanceDTO.PerformanceArtistDTO;
import hey.io.heyscheduler.domain.performance.dto.PerformanceDTO.PerformanceTicketingDTO;
import hey.io.heyscheduler.domain.performance.dto.QPerformanceDTO_PerformanceArtistDTO;
import hey.io.heyscheduler.domain.performance.entity.Performance;
import hey.io.heyscheduler.domain.performance.enums.PerformanceStatus;
import hey.io.heyscheduler.domain.push.enums.PushType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PerformanceQueryRepositoryImpl extends Querydsl5RepositorySupport implements PerformanceQueryRepository {

    public PerformanceQueryRepositoryImpl() {
        super(Performance.class);
    }

    /**
     * <p>출연 아티스트 목록</p>
     *
     * @param performanceIds 공연 ID 목록
     * @return 아티스트 목록
     */
    @Override
    public List<PerformanceArtistDTO> selectPerformanceArtistList(List<Long> performanceIds) {
        return select(new QPerformanceDTO_PerformanceArtistDTO(
            artist.artistId, artist.name.as("artistName")))
            .from(performance)
            .join(performance.performanceArtists, performanceArtist)
            .join(performanceArtist.artist, artist)
            .where(
                performance.performanceStatus.in(PerformanceStatus.READY, PerformanceStatus.ONGOING),
                artist.artistStatus.eq(ArtistStatus.ENABLE),
                performance.performanceId.in(performanceIds)
            )
            .fetch();
    }

    /**
     * <p>티켓 예매 공연 목록</p>
     *
     * @param currentDateTime 조회 기준 시각
     * @return 공연 목록
     */
    @Override
    public List<PerformanceTicketingDTO> selectPerformanceTicketingList(LocalDateTime currentDateTime) {
        // 1. 공연별 티켓 오픈 시간 조회
        List<PerformanceTicketingDTO> ticketingList = getOpenTicketingList(currentDateTime);

        // 2. 해당 공연 정보(id + name) 조회
        List<Long> performanceIds = ticketingList.stream().map(PerformanceTicketingDTO::getPerformanceId).toList();
        List<PerformanceTicketingDTO> performanceTicketingList = select(
            Projections.fields(PerformanceTicketingDTO.class,
                performance.performanceId,
                performance.name.as("performanceName")))
            .from(performance)
            .where(performanceIdIn(performanceIds))
            .fetch();

        // 3. DTO 정보 조합
        ticketingList.forEach(ticketing -> performanceTicketingList.stream()
            .filter(performance -> performance.getPerformanceId().equals(ticketing.getPerformanceId()))
            .findFirst()
            .ifPresent(performance -> ticketing.setPerformanceName(performance.getPerformanceName())));

        return ticketingList;
    }

    /**
     * <p>공연별 티켓 오픈 목록</p>
     *
     * @return 예매 당일, D-1일 공연 - 가장 빠른 예매 시간과 공연 매핑 목록
     */
    private List<PerformanceTicketingDTO> getOpenTicketingList(LocalDateTime currentDateTime) {
        // 1. ticketing 데이터 조회
        List<PerformanceTicketingDTO> ticketingList = select(
            Projections.fields(PerformanceTicketingDTO.class,
                performanceTicketing.performance.performanceId,
                performanceTicketing.openDatetime.min().as("openDatetime")))
            .from(performanceTicketing)
            .where(performanceTicketing.openDatetime.isNotNull())
            .groupBy(performanceTicketing.performance.performanceId)
            .fetch();

        // 2. 예매 당일, D-1일 공연 필터링
        return ticketingList.stream()
            .filter(ticketing -> {
                // 2-1. 예매 당일 공연 : 티켓 오픈 시간과 현재 시간의 차이가 1분 이내
                long difference = Duration.between(ticketing.getOpenDatetime(), currentDateTime).getSeconds();
                if (Math.abs(difference) <= 60) {
                    ticketing.setTicketingType(PushType.BATCH_TICKETING_OPEN);
                    return true;
                }
                // 2-2. 예매 D-1일 공연 : 티켓 오픈 시간에서 1일을 뺀 값과 현재 시간의 차이가 1분 이내
                long differenceIn1day = Duration.between(ticketing.getOpenDatetime().minusDays(1), currentDateTime).getSeconds();
                if (Math.abs(differenceIn1day) <= 60) {
                    ticketing.setTicketingType(PushType.BATCH_TICKETING_D1);
                    return true;
                }
                return false;
            }).toList();
    }

    private BooleanExpression performanceIdIn(List<Long> performanceIds) {
        return !performanceIds.isEmpty() ? performance.performanceId.in(performanceIds) : null;
    }
}