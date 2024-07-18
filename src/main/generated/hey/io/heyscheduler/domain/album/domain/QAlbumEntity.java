package hey.io.heyscheduler.domain.album.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlbumEntity is a Querydsl query type for AlbumEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlbumEntity extends EntityPathBase<AlbumEntity> {

    private static final long serialVersionUID = -1529556776L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlbumEntity albumEntity = new QAlbumEntity("albumEntity");

    public final hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate _super = new hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate(this);

    public final StringPath albumImage = createString("albumImage");

    public final hey.io.heyscheduler.domain.artist.domain.QArtistEntity artist;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath id = createString("id");

    public final StringPath releaseDate = createString("releaseDate");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAlbumEntity(String variable) {
        this(AlbumEntity.class, forVariable(variable), INITS);
    }

    public QAlbumEntity(Path<? extends AlbumEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlbumEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlbumEntity(PathMetadata metadata, PathInits inits) {
        this(AlbumEntity.class, metadata, inits);
    }

    public QAlbumEntity(Class<? extends AlbumEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.artist = inits.isInitialized("artist") ? new hey.io.heyscheduler.domain.artist.domain.QArtistEntity(forProperty("artist")) : null;
    }

}

