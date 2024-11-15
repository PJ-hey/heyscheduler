package hey.io.heyscheduler.domain.performance.repository;

import static hey.io.heyscheduler.domain.artist.entity.QArtist.artist;
import static hey.io.heyscheduler.domain.performance.entity.QPerformance.performance;
import static hey.io.heyscheduler.domain.performance.entity.QPerformanceArtist.performanceArtist;

import hey.io.heyscheduler.common.repository.Querydsl5RepositorySupport;
import hey.io.heyscheduler.domain.artist.enums.ArtistStatus;
import hey.io.heyscheduler.domain.performance.dto.PerformanceArtistDTO;
import hey.io.heyscheduler.domain.performance.dto.QPerformanceArtistDTO;
import hey.io.heyscheduler.domain.performance.entity.Performance;
import hey.io.heyscheduler.domain.performance.enums.PerformanceStatus;
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
        return select(new QPerformanceArtistDTO(artist.artistId, artist.name.as("artistName")))
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
}