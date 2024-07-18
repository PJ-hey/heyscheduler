package hey.io.heyscheduler.domain.artist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArtistEntity is a Querydsl query type for ArtistEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArtistEntity extends EntityPathBase<ArtistEntity> {

    private static final long serialVersionUID = -1655636034L;

    public static final QArtistEntity artistEntity = new QArtistEntity("artistEntity");

    public final hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate _super = new hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate(this);

    public final ListPath<hey.io.heyscheduler.domain.album.domain.AlbumEntity, hey.io.heyscheduler.domain.album.domain.QAlbumEntity> albums = this.<hey.io.heyscheduler.domain.album.domain.AlbumEntity, hey.io.heyscheduler.domain.album.domain.QAlbumEntity>createList("albums", hey.io.heyscheduler.domain.album.domain.AlbumEntity.class, hey.io.heyscheduler.domain.album.domain.QAlbumEntity.class, PathInits.DIRECT2);

    public final StringPath artistImage = createString("artistImage");

    public final StringPath artistName = createString("artistName");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<String, StringPath> genre = this.<String, StringPath>createList("genre", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath id = createString("id");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QArtistEntity(String variable) {
        super(ArtistEntity.class, forVariable(variable));
    }

    public QArtistEntity(Path<? extends ArtistEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArtistEntity(PathMetadata metadata) {
        super(ArtistEntity.class, metadata);
    }

}

