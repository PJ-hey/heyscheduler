package hey.io.heyscheduler.domain.performance.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPerformance is a Querydsl query type for Performance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPerformance extends EntityPathBase<Performance> {

    private static final long serialVersionUID = 667893173L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPerformance performance = new QPerformance("performance");

    public final hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate _super = new hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate(this);

    public final StringPath age = createString("age");

    public final StringPath cast = createString("cast");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final StringPath id = createString("id");

    public final ListPath<PerformanceArtist, QPerformanceArtist> performanceArtists = this.<PerformanceArtist, QPerformanceArtist>createList("performanceArtists", PerformanceArtist.class, QPerformanceArtist.class, PathInits.DIRECT2);

    public final QPlace place;

    public final StringPath poster = createString("poster");

    public final StringPath price = createString("price");

    public final StringPath runtime = createString("runtime");

    public final StringPath schedule = createString("schedule");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<hey.io.heyscheduler.domain.performance.domain.enums.PerformanceStatus> status = createEnum("status", hey.io.heyscheduler.domain.performance.domain.enums.PerformanceStatus.class);

    public final StringPath storyUrls = createString("storyUrls");

    public final StringPath theater = createString("theater");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final BooleanPath visit = createBoolean("visit");

    public QPerformance(String variable) {
        this(Performance.class, forVariable(variable), INITS);
    }

    public QPerformance(Path<? extends Performance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPerformance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPerformance(PathMetadata metadata, PathInits inits) {
        this(Performance.class, metadata, inits);
    }

    public QPerformance(Class<? extends Performance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.place = inits.isInitialized("place") ? new QPlace(forProperty("place")) : null;
    }

}

