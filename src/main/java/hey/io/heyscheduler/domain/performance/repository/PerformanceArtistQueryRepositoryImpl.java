
package hey.io.heyscheduler.domain.performance.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heyscheduler.domain.artist.dto.ArtistListResponse;
import hey.io.heyscheduler.domain.artist.dto.QArtistListResponse;
import hey.io.heyscheduler.domain.performance.domain.QPerformanceArtist;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class PerformanceArtistQueryRepositoryImpl implements PerformanceArtistQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ArtistListResponse> getArtistsByPerformanceStartDate() {
        return ((JPAQuery)((JPAQuery)this.queryFactory.select(new QArtistListResponse(QPerformanceArtist.performanceArtist.artist.id, QPerformanceArtist.performanceArtist.artist.artistName, QPerformanceArtist.performanceArtist.artist.artistImage)).from(QPerformanceArtist.performanceArtist)).where(QPerformanceArtist.performanceArtist.performance.startDate.eq(LocalDate.now().plusDays(1L)))).fetch();
    }


}
