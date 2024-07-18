package hey.io.heyscheduler.domain.performance.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoxOfficeRank is a Querydsl query type for BoxOfficeRank
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoxOfficeRank extends EntityPathBase<BoxOfficeRank> {

    private static final long serialVersionUID = 1473509752L;

    public static final QBoxOfficeRank boxOfficeRank = new QBoxOfficeRank("boxOfficeRank");

    public final hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate _super = new hey.io.heyscheduler.common.entity.QBaseEntityWithUpdate(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath performanceIds = createString("performanceIds");

    public final EnumPath<hey.io.heyscheduler.domain.performance.domain.enums.TimePeriod> timePeriod = createEnum("timePeriod", hey.io.heyscheduler.domain.performance.domain.enums.TimePeriod.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QBoxOfficeRank(String variable) {
        super(BoxOfficeRank.class, forVariable(variable));
    }

    public QBoxOfficeRank(Path<? extends BoxOfficeRank> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoxOfficeRank(PathMetadata metadata) {
        super(BoxOfficeRank.class, metadata);
    }

}

