package hey.io.heyscheduler.domain.performance.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformanceArtist is a Querydsl query type for PerformanceArtist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformanceArtist extends EntityPathBase<PerformanceArtist> {

    private static final long serialVersionUID = -1998770852L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerformanceArtist performanceArtist = new QPerformanceArtist("performanceArtist");

    public final hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate _super = new hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate(this);

    public final hey.io.heyscheduler.domain.artist.domain.QArtistEntity artist;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPerformance performance;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPerformanceArtist(String variable) {
        this(PerformanceArtist.class, forVariable(variable), INITS);
    }

    public QPerformanceArtist(Path<? extends PerformanceArtist> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerformanceArtist(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerformanceArtist(PathMetadata metadata, PathInits inits) {
        this(PerformanceArtist.class, metadata, inits);
    }

    public QPerformanceArtist(Class<? extends PerformanceArtist> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.artist = inits.isInitialized("artist") ? new hey.io.heyscheduler.domain.artist.domain.QArtistEntity(forProperty("artist")) : null;
        this.performance = inits.isInitialized("performance") ? new QPerformance(forProperty("performance"), inits.get("performance")) : null;
    }

}

