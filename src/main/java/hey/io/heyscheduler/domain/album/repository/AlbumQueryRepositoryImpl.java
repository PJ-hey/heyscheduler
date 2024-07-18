

package hey.io.heyscheduler.domain.album.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heyscheduler.domain.album.domain.QAlbumEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AlbumQueryRepositoryImpl implements AlbumQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<String> findAllIds() {
        return ((JPAQuery)this.queryFactory.select(QAlbumEntity.albumEntity.id).from(QAlbumEntity.albumEntity)).fetch();
    }


}
