package hey.io.heyscheduler.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseEntityWithUpdate is a Querydsl query type for BaseEntityWithUpdate
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseEntityWithUpdate extends EntityPathBase<BaseEntityWithUpdate> {

    private static final long serialVersionUID = -775275356L;

    public static final QBaseEntityWithUpdate baseEntityWithUpdate = new QBaseEntityWithUpdate("baseEntityWithUpdate");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBaseEntityWithUpdate(String variable) {
        super(BaseEntityWithUpdate.class, forVariable(variable));
    }

    public QBaseEntityWithUpdate(Path<? extends BaseEntityWithUpdate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseEntityWithUpdate(PathMetadata metadata) {
        super(BaseEntityWithUpdate.class, metadata);
    }

}

