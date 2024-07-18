package hey.io.heyscheduler.domain.artist.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heyscheduler.domain.artist.domain.QArtistEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ArtistQueryRepositoryImpl implements ArtistQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<String> findAllIds() {
        return ((JPAQuery)this.queryFactory.select(QArtistEntity.artistEntity.id).from(QArtistEntity.artistEntity)).fetch();
    }


}
